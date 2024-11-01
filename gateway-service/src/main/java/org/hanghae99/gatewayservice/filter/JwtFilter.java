package org.hanghae99.gatewayservice.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hanghae99.gatewayservice.config.RedisDao;
import org.hanghae99.gatewayservice.entity.UserRoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {
    private final RedisDao redisDao;

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 토큰 만료시간
    private final long ACCESS_TOKEN_TIME = 60 * 30 * 1000L; // 30분

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    public JwtFilter(RedisDao redisDao) {
        super(Config.class);
        this.redisDao = redisDao;
    }

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(jwtSecret);
        key = Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) return chain.filter(exchange);

            String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).getFirst();
            if (!authHeader.startsWith("Bearer ")) return handleUnauthorized(response);

            String token = authHeader.substring(7);

            String blackList = redisDao.getBlackList(token);
            if (blackList != null) {
                if (blackList.equals("logout")) throw new IllegalArgumentException("Please Login again.");
            }

            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

                log.info("JWT Claims: ");
                ServerHttpRequest.Builder mutatedRequest = request.mutate();
                for (Map.Entry<String, Object> entry : claims.entrySet()) {
                    String claimKey = "X-Claim-" + entry.getKey();
                    String claimValue = String.valueOf(entry.getValue());
                    mutatedRequest.header(claimKey, claimValue);
                    log.info("{}: {}", claimKey, claimValue);
                }

                request = mutatedRequest.build();
                exchange = exchange.mutate().request(request).build();

            } catch (SecurityException | MalformedJwtException e) {
                logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            } catch (ExpiredJwtException e) {
                log.info("JWT AccessToken ReIssue");
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
                String passwordVersion = claims.get("passwordVersion", String.class);
                if (redisDao.hasKey(passwordVersion)) {
                    String username = claims.getSubject();
                    UserRoleEnum role = UserRoleEnum.valueOf(claims.get("auth", String.class));
                    String secChUaPlatform = claims.get("platform", String.class);
                    String accessToken = createToken(username, role, ACCESS_TOKEN_TIME, secChUaPlatform, passwordVersion);
                    response.getHeaders().add(HttpHeaders.AUTHORIZATION, accessToken);
                } else logger.error("Expired JWT token, 만료된 JWT token 입니다.");
            } catch (UnsupportedJwtException e) {
                logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            } catch (IllegalArgumentException e) {
                logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            }

            log.info("Custom PRE filter: request uri -> {}", request.getURI());
            log.info("Custom PRE filter: request id -> {}", request.getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST filter: response status code -> {}", response.getStatusCode());
            }));
        };
    }

    // 토큰 생성
    public String createToken(String username, UserRoleEnum role, Long tokenExpireTime, String platform, String passwordVersion) {
        Date date = new Date();
        return "Bearer " +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim("Authorization", role) // 사용자 권한
                        .claim("platform", platform) // 다중 디바이스 로그인을 위한 플랫폼 입력
                        .claim("passwordVersion", passwordVersion) // 비밀번호 변경 시 모든 기기 로그아웃
                        .setExpiration(new Date(date.getTime() + tokenExpireTime)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    private Mono<Void> handleUnauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"error\": \"%s\", \"message\": \"%s\"}", HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Invalid Authorization header format.");
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Data
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }
}

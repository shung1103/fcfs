package org.hanghae99.fcfs.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.common.config.RedisDao;
import org.hanghae99.fcfs.common.dto.TokenResponse;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    //JWT 데이터
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long ACCESS_TOKEN_TIME = 60 * 30 * 1000L; // 30분
    private static final long REFRESH_TOKEN_TIME = 1000 * 60 * 60 * 24 * 7L;// 7일

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final RedisDao redisDao;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기 Keys.hmacShaKeyFor(bytes);
    public String resolveToken(HttpServletRequest request) {
        String bearerToken= request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 생성
    public String createToken(String username, UserRoleEnum role, Long tokenExpireTime, String platform, Integer passwordChangeCount) {
        Date date = new Date();
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .claim("platform", platform) // 다중 디바이스 로그인을 위한 플랫폼 입력
                        .claim("passwordChangeCount", passwordChangeCount) // 비밀번호 변경 시 모든 기기 로그아웃
                        .setExpiration(new Date(date.getTime() + tokenExpireTime)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    // 유저 로그인 후 토큰 발행
    public TokenResponse createTokenByLogin(String username, UserRoleEnum role, String platform, Integer passwordChangeCount) {
        String accessToken = createToken(username, role, ACCESS_TOKEN_TIME, platform, passwordChangeCount);
        String refreshToken = createToken(username, role, REFRESH_TOKEN_TIME, platform, passwordChangeCount);
        redisDao.setRefreshToken(username, refreshToken, REFRESH_TOKEN_TIME);
        return new TokenResponse(accessToken, refreshToken);
    }

    //AccessToken 재발행 + refreshToken 함께 발행
    public void reissueAtk(String username, UserRoleEnum role, String reToken, HttpServletRequest request, Integer passwordChangeCount) {
        // 레디스 저장된 리프레쉬토큰값을 가져와서 입력된 reToken 같은지 유무 확인
        if (!redisDao.getRefreshToken(username).equals(reToken)) {
            throw new IllegalArgumentException();
        }
        String secChUaPlatform = request.getHeader("Sec-Ch-Ua-Platform");
        String accessToken = createToken(username, role, ACCESS_TOKEN_TIME, secChUaPlatform, passwordChangeCount);
        String refreshToken = createToken(username, role, REFRESH_TOKEN_TIME, secChUaPlatform, passwordChangeCount);
        redisDao.setRefreshToken(username, refreshToken, REFRESH_TOKEN_TIME);
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) throws ExpiredJwtException {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 액셋스 토큰의 만료시간 조회
    public Long getExpiration(String accessToken){
        //에세스 토큰 만료시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody()
                .getExpiration();
        //현재시간
        long now = new Date().getTime();
        return (expiration.getTime()-now);
    }
}
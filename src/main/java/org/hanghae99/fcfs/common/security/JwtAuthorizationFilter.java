package org.hanghae99.fcfs.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae99.fcfs.auth.repository.RedisRefreshTokenRepository;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        //토큰 원본 가져오기
        String tokenValue = jwtUtil.getTokenFromRequest(req);
        String token = jwtUtil.resolveToken(req);
        if(token != null) {
            if(!jwtUtil.validateToken(token)){
                ApiResponseDto responseDto = new ApiResponseDto("토큰이 유효하지 않습니다.", HttpStatus.BAD_REQUEST.value());
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.setContentType("application/json; charset=UTF-8");
                return;
            }
            Claims info = jwtUtil.getUserInfoFromToken(token);
            setAuthentication(info.getSubject());
        }

        if (StringUtils.hasText(tokenValue)) {
            // JWT 토큰 bearer 자르기
            tokenValue = jwtUtil.substringToken(tokenValue);
            log.info(tokenValue);

            //토큰 만료되었는지 여부 판별
            if (!jwtUtil.validateToken(tokenValue)) {
                // accessToken 만료되었으나 refreshToken 존재 여부 판별해본다.
                //토큰을 분해해서 Claim객체를 리턴받아온다. 그 안에 sub필드는 username 정보를 담고있음.
                Claims userInfo = jwtUtil.getUserInfoFromToken(tokenValue);
                String username = userInfo.getSubject();
                if (username != null) {
                    Optional<String> validRefreshToken = redisRefreshTokenRepository.findValidRefreshTokenByUsername(username);

                    //리프레시 토큰이 존재한다면
                    if (validRefreshToken.isPresent()) {
                        //엑세스토큰을 재발급 해줘야 한다.
                        log.info("리프레시 토큰 : "+ validRefreshToken);
                        //Claims안의 auth필드에 권한정보가 담겨있다.
                        String auth = userInfo.get("auth", String.class);
                        UserRoleEnum role = UserRoleEnum.valueOf(auth);


                        String newAccessToken = jwtUtil.createToken(username, role);

                        //토큰 보내기
                        jwtUtil.addJwtToCookie(newAccessToken, res);

                        //스웨거는 헤더에 토큰이있어야한다.
                        res.addHeader("Authorization",newAccessToken);
                        log.info("토큰 전송 완료 : "+ newAccessToken);

                        String substringToken = jwtUtil.substringToken(newAccessToken);


                        //새로운 엑세스토큰을 인증정보 저장하기
                        Claims info = jwtUtil.getUserInfoFromToken(substringToken);
                        setAuthentication(info.getSubject());
                        log.info("인증 최신화 완료 : "+ info.getSubject());
                    } else {
                        jwtExceptionHandler(res, "리프레시 토큰이 만료되었거나 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
                        return;
                    }
                } else {
                    jwtExceptionHandler(res, "엑세스토큰이 유효하지 않거나 유저정보가 없습니다.", HttpStatus.BAD_REQUEST);
                    return;
                }
            }else {
                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
                String username = info.getSubject();
                Optional<String> validRefreshToken = redisRefreshTokenRepository.findValidRefreshTokenByUsername(username);

                if (validRefreshToken.isPresent()) {
                    try {
                        setAuthentication(info.getSubject());
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return;
                    }
                } else {
                    jwtExceptionHandler(res, "리프레시 토큰이 만료되었거나 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
                    return;
                }
            }
        }
        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // Jwt 예외처리
    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new ApiResponseDto(msg, status.value()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

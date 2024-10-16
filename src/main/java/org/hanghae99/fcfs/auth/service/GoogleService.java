package org.hanghae99.fcfs.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae99.fcfs.auth.dto.SocialUserInfoDto;
import org.hanghae99.fcfs.auth.repository.RedisRefreshTokenRepository;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.common.security.JwtUtil;
import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "Google Login")
@Service
@RequiredArgsConstructor
public class GoogleService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // 수동 등록한 Bean
    private final JwtUtil jwtUtil;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.secret.id}")
    private String googleSecretId;


    public String googleLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String[] tokens = getToken(code);

        // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
        SocialUserInfoDto googleUserInfoDto = getUserInfo(tokens[0]);

        // 3. 필요시에 회원가입
        User googleUser = registerGoogleUserIfNeeded(googleUserInfoDto);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(googleUser.getUsername(), googleUser.getRole());

        // 5.기존의 토큰이 있다면 삭제
        redisRefreshTokenRepository.findByUsername(googleUser.getUsername())
                .ifPresent(redisRefreshTokenRepository::deleteRefreshToken);

        // 6.리프레시 토큰 저장
        redisRefreshTokenRepository.generateRefreshToken(googleUser.getUsername());

        return createToken;
    }


    // 애플리케이션은 인증 코드로 구글 서버에 토큰을 요청하고, 토큰을 전달 받습니다.
    // 1) 액세스 토큰 요청 메서드
    public String[] getToken(String code) throws JsonProcessingException {
        log.info("getToken code: " + code);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://oauth2.googleapis.com")
                .path("/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", googleClientId );
        body.add("client_secret", googleSecretId);
        body.add("redirect_uri", "http://localhost:8080/api/auth/google/login"); // 애플리케이션 등록시 설정한 redirect_uri
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri) // body 가 있으므로 post 메서드
                .headers(headers)
                .body(body);

        // HTTP 응답
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class // 반환값 타입은 String
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 값을 반환합니다.
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String[] res = new String[2];
        res[0] = jsonNode.get("access_token").asText();
        return res;
    }


    // 2) 인가 토큰을 통해 사용자 정보 가져오기
    private SocialUserInfoDto getUserInfo(String accessToken) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);


        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                entity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        String id = jsonNode.get("id").asText();
        String username = jsonNode.get("email").asText();
        String email = jsonNode.get("email").asText();
        String nickname = jsonNode.get("email").asText();
        String social = "GOOGLE";


        return new SocialUserInfoDto(id, username, email, nickname, social);
    }

    // 3) 구글 ID 정보로 회원가입
    private User registerGoogleUserIfNeeded(SocialUserInfoDto googleUserInfoDto) {
        // DB 에 중복된 구글 Id 가 있는지 확인
        String googleId = googleUserInfoDto.getId();
        String social = googleUserInfoDto.getSocial();
        User googleUser = userRepository.findBySocialIdAndSocial(googleId, social).orElse(null);

        if (googleUser == null) {
            // 구글 사용자 email 동일한 email 가진 회원이 있는지 확인
            String googleUsername = googleUserInfoDto.getEmail();
            User sameEmailUser = userRepository.findByEmail(googleUsername).orElse(null);
            if (sameEmailUser != null) {
                googleUser = sameEmailUser;
                // 기존 회원정보에 구글 Id 추가
                googleUser = googleUser.socialUpdate(googleId, social);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: 구글 email
                String email = googleUserInfoDto.getEmail();
                String username = email;
                String nickname = email;
                googleUser = new User(username, encodedPassword, UserRoleEnum.USER, email, googleId, social);
            }
            userRepository.save(googleUser);
        }
        return googleUser;
    }
}
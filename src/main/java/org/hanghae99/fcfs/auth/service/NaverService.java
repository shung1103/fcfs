package org.hanghae99.fcfs.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae99.fcfs.auth.dto.SocialUserInfoDto;
import org.hanghae99.fcfs.auth.repository.RedisRefreshTokenRepository;
import org.hanghae99.fcfs.common.config.VigenereCipher;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.common.security.JwtUtil;
import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "NAVER Login")
@Service
@RequiredArgsConstructor
public class NaverService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    private final VigenereCipher vigenereCipher;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.secret.id}")
    private String naverSecretId;

    public String naverLogin(String code) throws JsonProcessingException {
        // 여기까지는 들어옴
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String[] tokens = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        SocialUserInfoDto naverUserInfo = getNaverUserInfo(tokens[0]);

        // 3. 필요시에 회원 가입
        User naverUser = registerNaverUserIfNeeded(naverUserInfo);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(naverUser.getUsername(), naverUser.getRole());

        // 5.기존의 토큰이 있다면 삭제
        redisRefreshTokenRepository.findByUsername(naverUser.getUsername()).ifPresent(redisRefreshTokenRepository::deleteRefreshToken);

        // 6.리프레시 토큰 저장
        String refreshTokenKey = tokens[1] + ":refresh" + naverUser.getPasswordChangeCount();
        String createRefresh = redisRefreshTokenRepository.generateRefreshTokenInSocial(refreshTokenKey, naverUser.getUsername());

        return createToken;
    }

    private String[] getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://nid.naver.com")
                .path("/oauth2.0/token")
                .encode()
                .build()
                .toUri();
        //--------------//

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naverClientId);
        body.add("client_secret", naverSecretId);
        body.add("redirect_uri", "http://localhost:8080/api/auth/naver/login");
        body.add("code", code);
        body.add("state", "test");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String[] res = new String[2];
        res[0] = jsonNode.get("access_token").asText();
        res[1] = jsonNode.get("refresh_token").asText();
        return res;
    }

    private SocialUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/nid/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        String id = jsonNode.get("response").get("id").asText();
        String username = jsonNode.get("response").get("email").asText();
        String email = jsonNode.get("response").get("email").asText();
        String phone = jsonNode.get("response").get("mobile").asText();
        String name = jsonNode.get("response").get("name").asText();
        String social = "NAVER";

        return new SocialUserInfoDto(id, username, email, phone, social, name);
    }

    private User registerNaverUserIfNeeded(SocialUserInfoDto naverUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        String naverId = naverUserInfo.getId();
        String social = naverUserInfo.getSocial();
        User naverUser = userRepository.findBySocialIdAndSocial(naverId, social).orElse(null);

        naverId = VigenereCipher.encrypt(naverId, vigenereCipher.key);
        if (naverUser == null) {
            // 네이버 사용자  (username) 동일한  (username) 가진 회원이 있는지 확인
            String naverUsername = naverUserInfo.getEmail();
            User sameUsernameUser = userRepository.findByEmail(naverUsername).orElse(null);
            if (sameUsernameUser != null) {
                naverUser = sameUsernameUser;
                // 기존 회원정보에 카카오 Id 추가
                naverUser = naverUser.socialUpdate(naverId, social);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                //username으로 하기로 했음
                String email = VigenereCipher.encrypt(naverUserInfo.getEmail(), vigenereCipher.key);
                String phone = VigenereCipher.encrypt(naverUserInfo.getPhone(), vigenereCipher.key);
                String address = VigenereCipher.encrypt("need_update", vigenereCipher.key);
                String name = VigenereCipher.encrypt(naverUserInfo.getName(), vigenereCipher.key);
                naverUser = new User(naverUsername,  encodedPassword, UserRoleEnum.USER, email, naverId, social, phone, address, name);
            }
            userRepository.save(naverUser);
        }
        return naverUser;
    }

}

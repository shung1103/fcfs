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

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // 수동 등록한 Bean
    private final JwtUtil jwtUtil;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    @Value("${kakao.client.id}")
    private String kakaoClientId;
    public String kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String[] tokens = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        SocialUserInfoDto kakaoUserInfo = getKakaoUserInfo(tokens[0]);

        // 3. 필요시에 회원 가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(kakaoUser.getUsername(), kakaoUser.getRole());


        // 5.기존의 토큰이 있다면 삭제
        redisRefreshTokenRepository.findByUsername(kakaoUser.getUsername())
                .ifPresent(redisRefreshTokenRepository::deleteRefreshToken);

        // 6.레디스에 리프레시 토큰 저장
        String refreshTokenKey = tokens[1]+":refresh";
        redisRefreshTokenRepository.generateRefreshTokenInSocial(refreshTokenKey, kakaoUser.getUsername());

        return createToken;
    }


    // 애플리케이션은 인증 코드로 카카오 서버에 토큰을 요청하고, 토큰을 전달 받습니다.
    // 1) 액세스 토큰 요청 메서드
    public String[] getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id",kakaoClientId); // 자신의 REST API 키
        body.add("redirect_uri","https://burger-drop.shop/api/auth/kakao/login"); // 애플리케이션 등록시 설정한 redirect_uri
        body.add("code",code); // 인가 코드
        body.add("state", "test");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri) // body 가 있으므로 post 메서드
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class // 반환값 타입은 String
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 값을 반환합니다.
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String[] res = new String[2];
        res[0] = jsonNode.get("access_token").asText();
        res[1] = jsonNode.get("refresh_token").asText();
        return res;
    }

    // 2) 인가 토큰을 통해 사용자 정보 가져오기
    private SocialUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        // Http 요청 보내기
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

        String id = jsonNode.get("id").asText();
        String username = jsonNode.get("kakao_account")
                .get("email").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        String nickname = jsonNode.get("kakao_account")
                .get("email").asText();
        String social = "KAKAO";

        log.info("카카오 사용자 정보: " + id + ", " + email);

        return new SocialUserInfoDto(id, username,email, nickname,social);
    }


    // 3) 카카오 ID 정보로 회원가입
    private User registerKakaoUserIfNeeded(SocialUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        String kakaoId = kakaoUserInfo.getId();
        String social = kakaoUserInfo.getSocial();
        User kakaoUser = userRepository.findBySocialIdAndSocial(kakaoId,social).orElse(null);

        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoUsername = kakaoUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(kakaoUsername).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.socialUpdate(kakaoId, social);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);
                String email = kakaoUserInfo.getEmail();
                String username = email;
                String nickname = email;
                kakaoUser = new User(username,  encodedPassword, UserRoleEnum.USER, email,kakaoId, social);
            }
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
}
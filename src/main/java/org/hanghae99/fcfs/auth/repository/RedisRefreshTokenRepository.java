//package org.hanghae99.fcfs.auth.repository;
//
//import org.hanghae99.fcfs.user.entity.User;
//import org.hanghae99.fcfs.user.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.Cursor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ScanOptions;
//import org.springframework.stereotype.Repository;
//
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@Repository
//public class RedisRefreshTokenRepository {
//    private final UserRepository userRepository;
//    private final RedisTemplate<String, String> redisTemplate;
//    private final long expirationTimeSeconds; // refreshToken 만료 시간
//
//    //Redis 관련 설정을 초기화
//    public RedisRefreshTokenRepository(
//            UserRepository userRepository, RedisTemplate<String, String> redisTemplate,
//            @Value("${refresh.token.expiration.seconds}") long expirationTimeSeconds) {
//        this.userRepository = userRepository;
//        this.redisTemplate = redisTemplate;
//        this.expirationTimeSeconds = expirationTimeSeconds;
//    }
//
//    //일반 로그인 시 리프레시 토큰 저장, +구글
//    public void generateRefreshToken(String username) {
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new NullPointerException("User not found"));
//        String refreshToken = UUID.randomUUID() + ":refresh" + user.getPasswordChangeCount();
//        redisTemplate.opsForValue().set(refreshToken, username, expirationTimeSeconds, TimeUnit.SECONDS);
//    }
//
//    //네이버, 카카오 로그인 시 리프레시 토큰 저장
//    public String generateRefreshTokenInSocial(String refreshToken, String username) {
//        redisTemplate.opsForValue().set(refreshToken, username, expirationTimeSeconds, TimeUnit.SECONDS);
//        return refreshToken;
//    }
//
//    //리프레시 토큰 삭제 (로그아웃 시)
//    public void deleteRefreshToken(String username) {
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new NullPointerException("User not found"));
//        ScanOptions options = ScanOptions.scanOptions().match("*:refresh" + user.getPasswordChangeCount()).count(100).build();
//        Cursor<String> refreshTokenKeys = redisTemplate.scan(options);
//
//        while (refreshTokenKeys.hasNext()) {
//            String refreshToken = refreshTokenKeys.next();
//            String storedUsername = redisTemplate.opsForValue().get(refreshToken);
//
//            // 리프레시 토큰의 유저네임과 매칭되는 유저네임 찾기
//            if (storedUsername != null && storedUsername.equals(username)) redisTemplate.delete(refreshToken);
//        }
//        refreshTokenKeys.close();
//    }
//}
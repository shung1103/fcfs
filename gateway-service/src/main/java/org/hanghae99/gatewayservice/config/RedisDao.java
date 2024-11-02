package org.hanghae99.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisDao {
    private final RedisTemplate<String, String> redisTemplate;

    public String getRefreshToken(String key) {
        return  redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }

    public String getBlackList(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}

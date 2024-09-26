package com.example.goose.common.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token 저장
    public void storeRefreshToken(String uuid, String refreshToken, long expirationTimeMillis) {
        redisTemplate.opsForValue().set(uuid, refreshToken, expirationTimeMillis, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 가져오기
    public String getRefreshToken(String uuid) {
        return redisTemplate.opsForValue().get(uuid);
    }

    public boolean isValidRefreshToken(String uuid, String refreshToken) {
        String storedRefreshToken = redisTemplate.opsForValue().get(uuid);
        return storedRefreshToken != null && refreshToken.equals(storedRefreshToken);
    }

    // Refresh Token 삭제 (RTR 기법)
    public void deleteRefreshToken(String uuid) {
        redisTemplate.delete(uuid);
    }
}

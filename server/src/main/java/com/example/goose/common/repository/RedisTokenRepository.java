package com.example.goose.common.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisTokenRepository {

    private final StringRedisTemplate redisTemplate;

    public RedisTokenRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(String uuid, String refreshToken) {
        redisTemplate.opsForValue().set(uuid, refreshToken, Duration.ofDays(7)); // 7일간 유지
    }

    public String getRefreshToken(String uuid) {
        return redisTemplate.opsForValue().get(uuid);
    }
}
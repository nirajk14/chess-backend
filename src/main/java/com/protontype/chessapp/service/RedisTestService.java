package com.protontype.chessapp.service;


import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisTestService {

    private final StringRedisTemplate redisTemplate;

    public void testConnection() {
        redisTemplate.opsForValue().set("testKey", "Hello Redis!");
        String value = redisTemplate.opsForValue().get("testKey");
        System.out.println("✅ Redis test value: " + value);
    }
}

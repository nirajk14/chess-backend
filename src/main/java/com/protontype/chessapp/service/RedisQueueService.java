package com.protontype.chessapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisQueueService {
    private final StringRedisTemplate redis;
    private static final String QUEUE_KEY = "queue:main";
    private static final String LOCK_KEY = "lock:matchmaking";

    public void pushToQueue(Long userId) {
        redis.opsForList().rightPush(QUEUE_KEY, String.valueOf(userId));
    }

    public Long queueSize() {
        Long size = redis.opsForList().size(QUEUE_KEY);
        return size == null ? 0L : size;
    }

    public String acquireLock(long ttlSeconds) {
        String token = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent(LOCK_KEY, token, Duration.ofSeconds(ttlSeconds));
        if (Boolean.TRUE.equals(ok)) return token;
        return null;
    }

    public boolean releaseLock(String token) {
        String existing = redis.opsForValue().get(LOCK_KEY);
        if (token.equals(existing)) {
            redis.delete(LOCK_KEY);
            return true;
        }
        return false;
    }

    public Long popLeft() {
        String val = redis.opsForList().leftPop(QUEUE_KEY);
        if (val == null) return null;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void removeIfPresent(Long userId) {
        redis.opsForList().remove(QUEUE_KEY, 0, String.valueOf(userId));
    }
}

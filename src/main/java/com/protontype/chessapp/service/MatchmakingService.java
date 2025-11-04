package com.protontype.chessapp.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.protontype.chessapp.model.entity.Match;
import com.protontype.chessapp.model.entity.User;
import com.protontype.chessapp.model.entity.UserMatch;
import com.protontype.chessapp.repository.MatchRepository;
import com.protontype.chessapp.repository.UserMatchRepository;
import com.protontype.chessapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final RedisQueueService redisQueue;
    private final MatchRepository matchRepo;
    private final UserRepository userRepo;
    private final UserMatchRepository userMatchRepo;
    private final ObjectMapper objectMapper;

    // Keep SSE emitters per user (in-memory). Use external pub/sub for scale.
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // Public: register an SSE emitter for a user
    public void registerEmitter(Long userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(userId);
        });
    }

    // Public: join queue and attempt immediate match
    public void joinQueue(Long userId) {
        // Remove existing occurrences to avoid duplicates
        redisQueue.removeIfPresent(userId);
        // Push to queue
        redisQueue.pushToQueue(userId);

        // Try to match immediately
        tryMatch();
    }

    // Attempt to form matches if queue has >= 2
    public void tryMatch() {
        // Acquire short lock to avoid races
        String lockToken = redisQueue.acquireLock(5);
        if (lockToken == null) return; // busy, let other thread handle

        try {
            long size = redisQueue.queueSize();
            if (size < 2) return;

            // Pop two users (left-most = earliest)
            Long a = redisQueue.popLeft();
            Long b = redisQueue.popLeft();

            // Defensive: if one popped null, push back the other (if exists)
            if (a == null) {
                if (b != null) redisQueue.pushToQueue(b);
                return;
            }
            if (b == null) {
                redisQueue.pushToQueue(a);
                return;
            }

            createMatchForPair(a, b);

        } finally {
            redisQueue.releaseLock(lockToken);
        }
    }

    // Transactional DB write and SSE emission
    @Transactional
    protected void createMatchForPair(Long a, Long b) {
        // create match
        Match match = Match.builder()
                .status("PENDING")
                .startedAt(LocalDateTime.now())
                .build();
        match = matchRepo.save(match);
        // 2️⃣ Fetch user entities
        User userA = userRepo.findById(a)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + a));
        User userB = userRepo.findById(b)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + b));

        // create user_match rows (assign colors arbitrarily)
        UserMatch um1 = new UserMatch();
        um1.setMatch(match);
        um1.setUser(userA);
        um1.setColor("WHITE");
        um1.setMoves("");
        um1.setResult(null);
        um1.setEloChange(0);

        UserMatch um2 = new UserMatch();
        um2.setMatch(match);
        um2.setUser(userB);
        um2.setColor("BLACK");
        um2.setMoves("");
        um2.setResult(null);
        um2.setEloChange(0);

        userMatchRepo.saveAll(Arrays.asList(um1, um2));

        // Notify both players via SSE
        Map<String, Object> payloadA = Map.of(
                "matchId", match.getId(),
                "opponentId", b,
                "color", um1.getColor(),
                "status", match.getStatus()
        );
        Map<String, Object> payloadB = Map.of(
                "matchId", match.getId(),
                "opponentId", a,
                "color", um2.getColor(),
                "status", match.getStatus()
        );

        sendSseEvent(a, "match_found", payloadA);
        sendSseEvent(b, "match_found", payloadB);
    }

    protected void sendSseEvent(Long userId, String eventName, Object payload) {
        log.info("Sending SSE event {} to user {}", eventName, userId);
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;
        try {
            String json = objectMapper.writeValueAsString(payload);
            emitter.send(SseEmitter.event().name(eventName).data(json));
            log.info("Sent SSE event {} to user {}", eventName, userId);
        } catch (Exception e) {
            // emitter broken; cleanup
            try { emitter.completeWithError(e); } catch (Exception ignore) {}
            emitters.remove(userId);
            log.error("Failed to send SSE event {} to user {}", eventName, userId, e);
        }
    }
}

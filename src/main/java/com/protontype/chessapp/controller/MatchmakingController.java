package com.protontype.chessapp.controller;

import com.protontype.chessapp.model.dto.response.ApiResponse;
import com.protontype.chessapp.model.dto.response.JoinResponse;
import com.protontype.chessapp.security.JwtUtil;
import com.protontype.chessapp.service.MatchmakingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/matchmaking")
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingService matchmakingService;
    private final JwtUtil jwtUtil;

    @GetMapping(value = "/queue/{userId}/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamQueue(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);
        matchmakingService.registerEmitter(userId, emitter);

        // send initial searching event
        try {
            emitter.send(SseEmitter.event().name("searching").data("{\"status\":\"searching\"}"));
        } catch (Exception ignored) {
        }

        return emitter;
    }

    // Join endpoint
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<JoinResponse>> joinQueue(HttpServletRequest request) {
        // Extract token (assuming Bearer <token>)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(ApiResponse.failure("Missing or invalid token"));
        }

        String token = authHeader.substring(7);

        // Extract userId from token (implement this in your JWT utility)
        Long userId = jwtUtil.extractUserId(token);
        String username = jwtUtil.extractUsername(token);
        if (userId == null || username == null) {
            return ResponseEntity.ok(ApiResponse.failure("Invalid or expired token"));
        }

        matchmakingService.joinQueue(userId);
        JoinResponse joinResponse = JoinResponse.builder().userId(userId).username(username).joinedAt(LocalDateTime.now()).build();

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Joined matchmaking queue", joinResponse));
    }
}

package com.protontype.chessapp.controller;

import com.protontype.chessapp.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MatchingController {
    private final MatchingService matchingService;

    @MessageMapping("/join/{matchId}")
    public void joinMatch(@DestinationVariable Long matchId,
                          @Header("simpSessionAttributes") Map<String, Object> sessionAttrs) {
        Long userId = (Long) sessionAttrs.get("userId");
        String username = (String) sessionAttrs.get("username");

        matchingService.userJoined(matchId, userId, username);
    }
}

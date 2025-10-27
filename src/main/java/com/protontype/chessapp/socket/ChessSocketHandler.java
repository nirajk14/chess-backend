package com.protontype.chessapp.socket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChessSocketHandler extends TextWebSocketHandler {

    private final Map<String, Map<String, WebSocketSession>> matchSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String matchId = query.split("matchId=")[1].split("&")[0];

        matchSessions.putIfAbsent(matchId, new ConcurrentHashMap<>());
        matchSessions.get(matchId).put(session.getId(), session);

        broadcast(matchId, "User joined match " + matchId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String query = session.getUri().getQuery();
        String matchId = query.split("matchId=")[1].split("&")[0];

        // Relay message to all players in the same match
        broadcast(matchId, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String query = session.getUri().getQuery();
        String matchId = query.split("matchId=")[1].split("&")[0];

        matchSessions.getOrDefault(matchId, Map.of()).remove(session.getId());
        broadcast(matchId, "A user disconnected from match " + matchId);
    }

    private void broadcast(String matchId, String message) throws Exception {
        for (WebSocketSession sess : matchSessions.getOrDefault(matchId, Map.of()).values()) {
            if (sess.isOpen()) {
                sess.sendMessage(new TextMessage(message));
            }
        }
    }
}

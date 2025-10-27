package com.protontype.chessapp.config;

import com.protontype.chessapp.socket.ChessSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChessSocketHandler chessSocketHandler;

    public WebSocketConfig(ChessSocketHandler ChessSocketHandler) {
        this.chessSocketHandler = ChessSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chessSocketHandler, "/ws/game")
                .setAllowedOrigins("*"); // allow frontend connections
    }
}
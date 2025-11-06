package com.protontype.chessapp.model.domain;

import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

public class MatchRoom {
    @Getter
    private final Long matchId;
    @Getter
    private final ConcurrentHashMap<Long, String> players = new ConcurrentHashMap<>();
    private volatile boolean started = false;

    public MatchRoom(Long matchId) {
        this.matchId = matchId;
    }

    public void addPlayer(Long userId, String username) {
        players.putIfAbsent(userId, username);
    }

    public void removePlayer(Long userId) {
        players.remove(userId);
    }

    public synchronized boolean tryStartMatch() {
        if (players.size() == 2 && !started) {
            started = true;
            return true;
        }
        return false;
    }

}

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
        players.put(userId, username);
    }

    public void removePlayer(Long userId) {
        players.remove(userId);
    }

    public boolean isFull() {
        return players.size() == 2;
    }

    public boolean hasStarted() {
        return started;
    }

    public void startMatch() {
        this.started = true;
    }

}

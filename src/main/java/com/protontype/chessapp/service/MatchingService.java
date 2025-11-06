package com.protontype.chessapp.service;

import com.protontype.chessapp.model.domain.MatchRoom;
import com.protontype.chessapp.repository.MatchRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchingService {

    private final MatchRepository matchRepository;
    private final ConcurrentHashMap<Long, MatchRoom> matchRooms = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public MatchingService(MatchRepository matchRepository, SimpMessagingTemplate messagingTemplate) {
        this.matchRepository = matchRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void userJoined(Long matchId, Long userId, String username) {
        // 1️⃣ Create or get match room
        MatchRoom room = matchRooms.computeIfAbsent(matchId, MatchRoom::new);

        // 2️⃣ Add player
        room.addPlayer(userId, username);

        // 3️⃣ Notify current player they joined successfully
        messagingTemplate.convertAndSend(
                "/topic/match/" + matchId,
                username + " joined match " + matchId
        );

        // 4️⃣ If both joined, start match and notify all
        if (room.isFull() && !room.hasStarted()) {
            room.startMatch();
            matchRepository.findById(matchId).ifPresent(match -> {
                match.setStatus("ONGOING");
                matchRepository.save(match);
            });
            messagingTemplate.convertAndSend(
                    "/topic/match/" + matchId,
                    "✅ Match " + matchId + " started between: " + room.getPlayers().values()
            );
        }
    }

    public void userLeft(Long matchId, Long userId) {
        MatchRoom room = matchRooms.get(matchId);
        if (room != null) {
            room.removePlayer(userId);
            messagingTemplate.convertAndSend(
                    "/topic/match/" + matchId,
                    "❌ Player " + userId + " left match " + matchId
            );
            if (room.getPlayers().isEmpty()) {
                matchRooms.remove(matchId);
            }
        }
    }

    public Map<Long, String> getPlayersInMatch(Long matchId) {
        return matchRooms.getOrDefault(matchId, new MatchRoom(matchId)).getPlayers();
    }
}

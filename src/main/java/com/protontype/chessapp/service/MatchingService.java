package com.protontype.chessapp.service;

import com.protontype.chessapp.model.domain.MatchRoom;
import com.protontype.chessapp.model.entity.Match;
import com.protontype.chessapp.repository.MatchRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchingService {

    private final MatchValidationService matchValidationService;
    private final MatchRepository matchRepository;
    private final ConcurrentHashMap<Long, MatchRoom> matchRooms = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public MatchingService(MatchValidationService matchValidationService, MatchRepository matchRepository, SimpMessagingTemplate messagingTemplate) {
        this.matchValidationService = matchValidationService;
        this.matchRepository = matchRepository;
        this.messagingTemplate = messagingTemplate;
    }



    public void userJoined(Long matchId, Long userId, String username) {
       Optional<Match> validMatch = matchValidationService.getValidMatch(matchId, userId);
        if (validMatch.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/match/" + matchId,
                    "User not allowed or match doesn't exist.");
            return;
        }
        MatchRoom room = matchRooms.computeIfAbsent(matchId, MatchRoom::new);

        room.addPlayer(userId, username);
        messagingTemplate.convertAndSend(
                "/topic/match/" + matchId,
                username + " joined match " + matchId
        );
        if (room.tryStartMatch()) {
            validMatch.ifPresent(match -> {
                match.setStatus("ONGOING");
                matchRepository.save(match);
            });
            messagingTemplate.convertAndSend(
                    "/topic/match/" + matchId,
                    "Match " + matchId + " started between: " + room.getPlayers().values()
            );
        }
    }

    public void userLeft(Long matchId, Long userId) {
        MatchRoom room = matchRooms.get(matchId);
        if (room != null) {
            room.removePlayer(userId);
            messagingTemplate.convertAndSend(
                    "/topic/match/" + matchId,
                    "Player " + userId + " left match " + matchId
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

package com.protontype.chessapp.service;

import com.protontype.chessapp.model.entity.Match;
import com.protontype.chessapp.repository.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchValidationService {
    private final MatchRepository matchRepository;

    @Transactional
    public boolean validateAndJoin(Long matchId, Long userId, String username) {
        // DB operations, lazy collections safe
        Match match = matchRepository.findById(matchId).orElse(null);
        if (match == null) return false;

        boolean userExists = match.getUserMatches().stream()
                .anyMatch(um -> um.getUser().getId().equals(userId));

        if (!userExists) return false;

        // Update match status, etc.
        return true;
    }
}

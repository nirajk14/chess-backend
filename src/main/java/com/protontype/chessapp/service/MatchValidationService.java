package com.protontype.chessapp.service;

import com.protontype.chessapp.model.entity.Match;
import com.protontype.chessapp.repository.MatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchValidationService {
    private final MatchRepository matchRepository;

    @Transactional
    public Optional<Match> getValidMatch(Long matchId, Long userId) {
        return matchRepository.findById(matchId)
                .filter(match -> match.getUserMatches().stream()
                        .anyMatch(um -> um.getUser().getId().equals(userId)));
    }
}

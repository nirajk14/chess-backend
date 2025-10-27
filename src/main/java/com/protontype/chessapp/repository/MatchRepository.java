package com.protontype.chessapp.repository;

import com.protontype.chessapp.model.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}

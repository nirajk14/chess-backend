package com.protontype.chessapp.repository;

import com.protontype.chessapp.model.entity.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {
    List<UserMatch> findByMatchId(Long matchId);
    List<UserMatch> findByUserId(Long userId);
}

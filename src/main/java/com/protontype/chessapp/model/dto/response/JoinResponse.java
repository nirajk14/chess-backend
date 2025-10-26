package com.protontype.chessapp.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinResponse {
    private String username;
    private Long userId;
    private LocalDateTime joinedAt;
}

package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.dto.MatchHistoryDto;
import com.example.streetleague.dto.MatchResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ImatchHistoryService {

    // JPQL enrichi
    List<MatchHistoryDto> getMatchHistory(
            MatchStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

    // Keywords multi-table
    List<MatchResponse> searchMatchesByTeamAndStatus(
            String teamName,
            List<String> statusNames
    );
}
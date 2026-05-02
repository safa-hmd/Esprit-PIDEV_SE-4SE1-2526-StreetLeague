package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.ServiceInterface.ImatchHistoryService;
import com.example.streetleague.dto.MatchHistoryDto;
import com.example.streetleague.dto.MatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchHistoryServiceImpl implements ImatchHistoryService {

    private final MatchRepository matchRepository;

    // ── MÉTHODE 1 : JPQL enrichi ──────────────────────────────────────────
    @Override
    public List<MatchHistoryDto> getMatchHistory(
            MatchStatus status,
            LocalDateTime from,
            LocalDateTime to) {

        return matchRepository.findMatchHistoryEnriched(status, from, to);
    }

    // ── MÉTHODE 2 : Keywords multi-table ─────────────────────────────────
    @Override
    public List<MatchResponse> searchMatchesByTeamAndStatus(
            String teamName,
            List<String> statusNames) {

        List<MatchStatus> statuses = statusNames.stream()
                .map(MatchStatus::valueOf)
                .collect(Collectors.toList());

        String pattern = "%" + teamName + "%";

        return matchRepository.findMatchesByTeamAndStatus(pattern, statuses)
                .stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
// MatchSearchService.java
package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.dto.MatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchSearchService {

    private final MatchRepository matchRepository;

    public List<MatchResponse> searchMatches(String keyword, List<String> statusNames) {

        // Extraire un mot-clé propre s'il y a null ou espaces
        String cleanKeyword = (keyword == null) ? "" : keyword.trim();

        // 1. Recherche par Mots-Clés via Keyword function multiple-tables (Match + Team)
        List<Match> matches = matchRepository.findByTeamA_NameContainingIgnoreCaseOrTeamB_NameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrderByMatchDateDesc(
                cleanKeyword, cleanKeyword, cleanKeyword
        );

        // 2. Filtrer par Status si demandé (on le fait en stream post-récupération)
        if (statusNames != null && !statusNames.isEmpty()) {
            List<MatchStatus> statuses = statusNames.stream()
                    .map(MatchStatus::valueOf)
                    .toList();

            matches = matches.stream()
                    .filter(m -> statuses.contains(m.getStatus()))
                    .toList();
        }

        // 3. Convertir en DTO MatchResponse
        return matches.stream()
                .map(MatchResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
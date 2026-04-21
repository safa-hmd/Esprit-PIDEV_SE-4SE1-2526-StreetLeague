package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.MatchRequest;
import com.example.streetleague.dto.MatchResponse;
import com.example.streetleague.dto.MatchUpdateRequest;

import java.util.List;

public interface ImatchService {
    MatchResponse addMatch(MatchRequest m, Long teamAId, Long teamBId, Long captainId);
    MatchResponse updateMatch(MatchUpdateRequest m, Long captainId);
    void deleteMatch(Long idMatch, Long captainId);
    List<MatchResponse> ShowMatchs();
    MatchResponse ShowMatch(Long idMatch);
}
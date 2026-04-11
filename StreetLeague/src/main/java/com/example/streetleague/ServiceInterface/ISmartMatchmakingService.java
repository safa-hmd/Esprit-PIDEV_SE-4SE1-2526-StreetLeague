package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.MatchCandidateResponse;
import java.util.List;

public interface ISmartMatchmakingService {
    void updateEloAfterMatch(Long matchId);
    List<MatchCandidateResponse> findSmartOpponents(Long teamId);
}
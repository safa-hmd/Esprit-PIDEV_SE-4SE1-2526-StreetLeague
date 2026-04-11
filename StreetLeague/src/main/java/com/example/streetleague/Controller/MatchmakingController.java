package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.ISmartMatchmakingService;
import com.example.streetleague.dto.MatchCandidateResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("matchmaking")
public class MatchmakingController {

    private final ISmartMatchmakingService smartMatchmakingService;

    @GetMapping("suggest/{teamId}")
    public ResponseEntity<List<MatchCandidateResponse>> suggestOpponents(
            @PathVariable Long teamId) {
        return ResponseEntity.ok(
                smartMatchmakingService.findSmartOpponents(teamId));
    }

    @PutMapping("elo/{matchId}")
    public ResponseEntity<Void> updateEloAfterMatch(
            @PathVariable Long matchId) {
        smartMatchmakingService.updateEloAfterMatch(matchId);
        return ResponseEntity.noContent().build();
    }
}
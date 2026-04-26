package com.example.streetleague.Controller;


import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.ServiceImp.MatchmakingService;
import com.example.streetleague.dto.MatchCandidateResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchmaking")
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingService matchmakingService;
    private final TeamRepository teamRepository;

    @GetMapping("/candidates")
    @PreAuthorize("hasAnyRole('PLAYER', 'ADMIN', 'COACH')")
    public ResponseEntity<List<MatchCandidateResponse>> getCandidates(
            @RequestParam Long teamId,
            @RequestParam String location,
            @RequestParam(defaultValue = "5") int top
    ) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Équipe introuvable"));

        List<MatchCandidateResponse> candidates =
                matchmakingService.findCandidates(team, location, top);

        return ResponseEntity.ok(candidates);
    }

    // ← AJOUTE CECI
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(Exception e) {
        e.printStackTrace(); // ← tu verras l'erreur exacte dans les logs Spring
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

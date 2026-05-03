package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.ServiceImp.MatchmakingService;
import com.example.streetleague.dto.MatchCandidateResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchmaking")
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingService matchmakingService;
    private final TeamRepository teamRepository;

    @GetMapping("/candidates")
    public ResponseEntity<List<MatchCandidateResponse>> getCandidates(
            @RequestParam Long teamId,
            @RequestParam(defaultValue = "") String location,
            @RequestParam(defaultValue = "50") int top,
            @RequestParam(required = false) String sport
    ) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Équipe introuvable"));

        List<MatchCandidateResponse> candidates =
                matchmakingService.findCandidates(team, location, top, sport);

        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/sports")
    public ResponseEntity<List<String>> getAvailableSports() {
        List<String> sports = List.of(
                "FOOTBALL", "BASKETBALL", "TENNIS",
                "PADEL", "VOLLEYBALL", "OTHER"
        );
        return ResponseEntity.ok(sports);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
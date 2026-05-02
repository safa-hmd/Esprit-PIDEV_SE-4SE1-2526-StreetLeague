package com.example.streetleague.Controller;

import com.example.streetleague.dto.BracketResponseDTO;
import com.example.streetleague.dto.SubmitResultDTO;
import com.example.streetleague.Entity.BracketType;
import com.example.streetleague.Entity.TournamentMatch;
import com.example.streetleague.Repository.TournamentMatchRepository;
import com.example.streetleague.Repository.TournamentRepository;
import com.example.streetleague.ServiceImp.BracketgeneratorserviceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brackets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BracketController {

    private final BracketgeneratorserviceImpl bracketService;
    private final TournamentMatchRepository tournamentMatchRepository;
    private final TournamentRepository tournamentRepository;

    /**
     * POST /api/brackets/{tournamentId}/generate?type=SINGLE_ELIMINATION
     * Génère le bracket complet pour un tournoi
     */
    @PostMapping("/{tournamentId}/generate")
    public ResponseEntity<BracketResponseDTO> generate(
            @PathVariable Long tournamentId,
            @RequestParam(defaultValue = "SINGLE_ELIMINATION") BracketType type) {

        List<TournamentMatch> matches = bracketService.generateBracket(tournamentId, type);

        String name = tournamentRepository.findById(tournamentId)
                .map(t -> t.getName()).orElse("Tournament");

        return ResponseEntity.ok(BracketResponseDTO.from(tournamentId, name, matches));
    }

    /**
     * GET /api/brackets/{tournamentId}
     * Récupère le bracket existant
     */
    @GetMapping("/{tournamentId}")
    public ResponseEntity<BracketResponseDTO> getBracket(@PathVariable Long tournamentId) {
        List<TournamentMatch> matches = tournamentMatchRepository
                .findByTournamentIdOrderByRoundAscPositionAsc(tournamentId);

        if (matches.isEmpty())
            return ResponseEntity.notFound().build();

        String name = tournamentRepository.findById(tournamentId)
                .map(t -> t.getName()).orElse("Tournament");

        return ResponseEntity.ok(BracketResponseDTO.from(tournamentId, name, matches));
    }

    /**
     * PATCH /api/brackets/matches/{matchId}/result
     * Soumet le résultat d'un match → avancement automatique
     */
    @PatchMapping("/matches/{matchId}/result")
    public ResponseEntity<BracketResponseDTO.MatchSlotDTO> submitResult(
            @PathVariable Long matchId,
            @Valid @RequestBody SubmitResultDTO dto) {

        TournamentMatch updated = bracketService.submitResult(
                matchId, dto.getWinnerId(), dto.getWinnerIsTeam());

        // Mettre à jour les scores dans le Match JPA
        if (updated.getMatch() != null && dto.getScoreA() != null) {
            updated.getMatch().setScoreTeamA(dto.getScoreA());
            updated.getMatch().setScoreTeamB(dto.getScoreB());
        }

        return ResponseEntity.ok(BracketResponseDTO.MatchSlotDTO.from(updated));
    }

    /**
     * GET /api/brackets/{tournamentId}/round/{round}
     * Récupère un round spécifique
     */
    @GetMapping("/{tournamentId}/round/{round}")
    public ResponseEntity<List<BracketResponseDTO.MatchSlotDTO>> getRound(
            @PathVariable Long tournamentId,
            @PathVariable int round) {

        List<TournamentMatch> matches = tournamentMatchRepository
                .findByTournamentIdAndRound(tournamentId, round);

        if (matches.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(
                matches.stream().map(BracketResponseDTO.MatchSlotDTO::from).toList()
        );
    }
}
package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Tournament;
import com.example.streetleague.Repository.TournamentRepository;
import com.example.streetleague.ServiceInterface.ITournamentService;
import com.example.streetleague.dto.TournamentDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "*")
public class TournamentController {

    private final ITournamentService tournamentService;
    private final TournamentRepository tournamentRepository;

    public TournamentController(ITournamentService tournamentService, TournamentRepository tournamentRepository) {
        this.tournamentService = tournamentService;
        this.tournamentRepository = tournamentRepository;
    }

    // ===== ADMIN =====

    @PostMapping
    public ResponseEntity<TournamentDto> create(@Valid @RequestBody TournamentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tournamentService.createTournament(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentDto> update(@PathVariable Long id,
                                                @Valid @RequestBody TournamentDto dto) {
        return ResponseEntity.ok(tournamentService.updateTournament(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TournamentDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.cancelTournament(id));
    }

    // ===== ADMIN + PLAYER =====

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/legacy")
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return ResponseEntity.ok(tournamentRepository.findAll());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<TournamentDto>> getUpcoming() {
        return ResponseEntity.ok(tournamentService.getUpcomingTournaments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDto> getById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getTournamentById(id));
    }
}

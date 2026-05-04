package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.ITournamentRegistrationService;
import com.example.streetleague.dto.TournamentRegistrationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class TournamentRegistrationController {

    private final ITournamentRegistrationService registrationService;

    public TournamentRegistrationController(ITournamentRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // ===== PLAYER : inscription =====

    @PostMapping("/player")
    public ResponseEntity<TournamentRegistrationDto> registerPlayer(
            @RequestParam Long tournamentId,
            @RequestParam Long playerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.registerPlayer(tournamentId, playerId));
    }

    @PostMapping("/team")
    public ResponseEntity<TournamentRegistrationDto> registerTeam(
            @RequestParam Long tournamentId,
            @RequestParam Long teamId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.registerTeam(tournamentId, teamId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TournamentRegistrationDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.cancelRegistration(id));
    }

    // ===== ADMIN + PLAYER =====

    @GetMapping("/{id}")
    public ResponseEntity<TournamentRegistrationDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getRegistrationById(id));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<TournamentRegistrationDto>> getByTournament(
            @PathVariable Long tournamentId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByTournament(tournamentId));
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<TournamentRegistrationDto>> getByPlayer(
            @PathVariable Long playerId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByPlayer(playerId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TournamentRegistrationDto>> getByTeam(
            @PathVariable Long teamId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByTeam(teamId));
    }

    @GetMapping("/player/{playerId}/teams")
    public ResponseEntity<List<TournamentRegistrationDto>> getTeamRegistrationsByPlayer(
            @PathVariable Long playerId) {
        return ResponseEntity.ok(
                registrationService.getTeamRegistrationsByPlayer(playerId)
        );
    }

    // ===== ADMIN =====

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<TournamentRegistrationDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.acceptRegistration(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<TournamentRegistrationDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.rejectRegistration(id));
    }
}
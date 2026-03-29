package com.example.streetleague.Controller;

import com.example.streetleague.Entity.Tournament;
import com.example.streetleague.Repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TournamentController {

    private final TournamentRepository tournamentRepository;

    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return ResponseEntity.ok(tournamentRepository.findAll());
    }
}

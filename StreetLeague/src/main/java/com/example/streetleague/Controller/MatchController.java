package com.example.streetleague.Controller;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.ImatchService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.ChallengeRequest;
import com.example.streetleague.dto.MatchRequest;
import com.example.streetleague.dto.MatchResponse;
import com.example.streetleague.dto.MatchUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("match")
public class MatchController {

    private final ImatchService imatchService;
    private final UserRepository userRepository;

    public MatchController(ImatchService imatchService, UserRepository userRepository) {
        this.imatchService = imatchService;
        this.userRepository = userRepository;
    }

    // POST /match/add?teamAId=1&teamBId=2&email=captain@mail.com
    @PostMapping("/add")
    public MatchResponse addMatch(@RequestBody MatchRequest dto,
                                  @RequestParam Long teamAId,
                                  @RequestParam Long teamBId,
                                  @RequestParam String email) {
        User captain = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return imatchService.addMatch(dto, teamAId, teamBId, captain.getIdUser());
    }

    // PUT /match/update?email=captain@mail.com
    @PutMapping("update")
    public MatchResponse updateMatch(@RequestBody MatchUpdateRequest dto,
                                     @RequestParam String email) {
        User captain = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return imatchService.updateMatch(dto, captain.getIdUser());
    }

    // DELETE /match/delete/1?email=captain@mail.com
    @DeleteMapping("delete/{idMatch}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long idMatch,
                                         @RequestParam String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            imatchService.deleteMatch(idMatch, user.getIdUser());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /match/showMatchs
    @GetMapping("showMatchs")
    public List<MatchResponse> showMatchs() {
        return imatchService.ShowMatchs();
    }

    // GET /match/showMatchById/1
    @GetMapping("showMatchById/{idMatch}")
    public MatchResponse showMatch(@PathVariable Long idMatch) {
        return imatchService.ShowMatch(idMatch);
    }

    // Dans MatchController.java — AJOUTER cette méthode uniquement
    @PostMapping("add-by-email")
    public ResponseEntity<MatchResponse> addMatchByEmail(
            @RequestBody MatchRequest dto,
            @RequestParam Long teamAId,
            @RequestParam Long teamBId,
            @RequestParam String email) {

        // Résoudre l'email → userId
        User captain = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        MatchResponse response = imatchService.addMatch(dto, teamAId, teamBId, captain.getIdUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /match/{matchId}/respond?captainId=2&accept=true
    @PutMapping("{matchId}/respond")
    public ResponseEntity<MatchResponse> respondToMatch(
            @PathVariable Long matchId,
            @RequestParam Long captainId,
            @RequestParam boolean accept) {
        return ResponseEntity.ok(imatchService.respondToMatch(matchId, captainId, accept));
    }

    // MatchController.java
    @PostMapping("/challenge")
    public ResponseEntity<MatchResponse> challengeTeam(
            @RequestBody ChallengeRequest request,
            @RequestParam String email) {

        User captain = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        MatchResponse response = imatchService.addMatch(
                new MatchRequest(request.getMatchDate(), request.getLocation()),
                request.getChallengerTeamId(),
                request.getOpponentTeamId(),
                captain.getIdUser()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
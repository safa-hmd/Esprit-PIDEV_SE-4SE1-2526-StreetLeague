package com.example.streetleague.Controller;

import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.ImatchService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.MatchRequest;
import com.example.streetleague.dto.MatchResponse;
import com.example.streetleague.dto.MatchUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("match")
public class MatchController {

    ImatchService  imatchService;
    UserRepository userRepository;

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
    public void deleteMatch(@PathVariable Long idMatch,
                            @RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        imatchService.deleteMatch(idMatch, user.getIdUser());
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
}
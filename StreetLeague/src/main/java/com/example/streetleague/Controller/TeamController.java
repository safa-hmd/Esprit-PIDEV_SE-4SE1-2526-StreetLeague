package com.example.streetleague.Controller;

import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.IteamService;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TeamRequest;
import com.example.streetleague.dto.TeamResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/team")
public class TeamController {

    IteamService   teamService;
    UserRepository userRepository;
    TeamRepository teamRepository;

    // POST /team/add?email=captain@mail.com
    @PostMapping("/add")
    public TeamResponse addTeam(@RequestBody TeamRequest dto, @RequestParam String email) {
        User captain = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found: " + email));
        return teamService.addTeam(dto, captain.getIdUser());
    }

    @PutMapping("update/{teamId}")
    public TeamResponse updateTeam(@PathVariable Long teamId,
                                   @RequestBody TeamRequest dto,
                                   @RequestParam String email) {
        User captain = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return teamService.updateTeam(teamId, dto, captain.getIdUser());
    }

    @DeleteMapping("delete/{idTeam}")
    public void deleteTeam(@PathVariable Long idTeam,
                           @RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        System.out.println("=== DELETE ===");
        System.out.println("Email: " + email);
        System.out.println("UserId: " + user.getIdUser());
        System.out.println("Role: " + user.getRole());

        // ✅ ADMIN → supprime directement
        if (user.getRole() == Role.ADMIN) {
            teamRepository.deleteById(idTeam);
            return;
        }

        // ✅ Capitaine → passe son ID au service
        teamService.deleteTeam(idTeam, user.getIdUser());
    }
    // GET /team/showTeams
    @GetMapping("showTeams")
    public List<TeamResponse> showTeams() {
        return teamService.ShowTeams();
    }

    // GET /team/showTeamById/1
    @GetMapping("showTeamById/{idTeam}")
    public TeamResponse showTeam(@PathVariable Long idTeam) {
        return teamService.ShowTeam(idTeam);
    }

    // POST /team/1/join?email=player@mail.com
    @PostMapping("{idTeam}/join")
    public TeamResponse joinTeam(@PathVariable Long idTeam, @RequestParam String email) {
        User player = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found: " + email));
        return teamService.joinTeam(idTeam, player.getIdUser());
    }

    // DELETE /team/1/leave?email=player@mail.com
    @DeleteMapping("{idTeam}/leave")
    public TeamResponse leaveTeam(@PathVariable Long idTeam, @RequestParam String email) {
        User player = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found: " + email));
        return teamService.leaveTeam(idTeam, player.getIdUser());
    }
}
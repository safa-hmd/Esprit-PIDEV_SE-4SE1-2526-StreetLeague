package com.example.streetleague.Controller;

import com.example.streetleague.ServiceImp.PerformanceService;
import com.example.streetleague.dto.PlayerStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping("/player/{playerId}")
    public ResponseEntity<PlayerStatsDto> getPlayerStats(@PathVariable Long playerId) {
        return performanceService.getPlayerStats(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<PlayerStatsDto>> getGlobalLeaderboard() {
        return ResponseEntity.ok(performanceService.getGlobalLeaderboard());
    }

    @GetMapping("/team/{teamId}/leaderboard")
    public ResponseEntity<List<PlayerStatsDto>> getTeamLeaderboard(@PathVariable Long teamId) {
        return ResponseEntity.ok(performanceService.getTeamLeaderboard(teamId));
    }

    @GetMapping("/momentum")
    public ResponseEntity<List<PlayerStatsDto>> getGlobalMomentum() {
        return ResponseEntity.ok(performanceService.getGlobalMomentum());
    }

    @GetMapping("/fatigue-alerts")
    public ResponseEntity<List<PlayerStatsDto>> getGlobalFatigueAlerts() {
        return ResponseEntity.ok(performanceService.getGlobalFatigueAlerts());
    }

    /**
     * POST /api/performance/checkin?playerId=X&attendanceType=Y
     * Crée ou MET À JOUR le check-in du jour (on peut cliquer plusieurs fois).
     * Recalcule fatigue + streak à chaque appel.
     */
    @PostMapping("/checkin")
    public ResponseEntity<PlayerStatsDto> checkin(
            @RequestParam Long playerId,
            @RequestParam String attendanceType) {
        return ResponseEntity.ok(
                performanceService.checkin(playerId, attendanceType));
    }
}
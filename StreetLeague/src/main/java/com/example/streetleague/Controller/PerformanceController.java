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
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://streetleaguefrontend.azurewebsites.net"
})
public class PerformanceController {

    private final PerformanceService performanceService;

    // ── Endpoints existants (inchangés) ──────────────────────────────────

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

    @PostMapping("/checkin")
    public ResponseEntity<PlayerStatsDto> checkin(
            @RequestParam Long playerId,
            @RequestParam String attendanceType) {
        return ResponseEntity.ok(performanceService.checkin(playerId, attendanceType));
    }

    @GetMapping("/injury-risk")
    public ResponseEntity<List<PlayerStatsDto>> getInjuryRiskRanking() {
        return ResponseEntity.ok(performanceService.getInjuryRiskRanking());
    }

    @GetMapping("/consistency-ranking")
    public ResponseEntity<List<PlayerStatsDto>> getConsistencyRanking() {
        return ResponseEntity.ok(performanceService.getConsistencyRanking());
    }

    @GetMapping("/by-level/{level}")
    public ResponseEntity<List<PlayerStatsDto>> getPlayersByLevel(@PathVariable String level) {
        return ResponseEntity.ok(performanceService.getPlayersByPerformanceLevel(level));
    }

    @GetMapping("/predictions")
    public ResponseEntity<List<PlayerStatsDto>> getPerformancePredictions() {
        return ResponseEntity.ok(performanceService.getPerformancePredictions());
    }

    // ══════════════════════════════════════════════════════════════════════
    //  NOUVEAUX ENDPOINTS — ACWR
    // ══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/performance/acwr/{playerId}
     * Stats ACWR détaillées d'un joueur : ratio, charge aiguë/chronique, zone, recommandation.
     */
    @GetMapping("/acwr/{playerId}")
    public ResponseEntity<PlayerStatsDto> getAcwrStats(@PathVariable Long playerId) {
        return performanceService.getAcwrStats(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/performance/acwr/ranking
     * Classement global trié par ACWR décroissant.
     * Les joueurs les plus à risque de surentraînement apparaissent en premier.
     */
    @GetMapping("/acwr/ranking")
    public ResponseEntity<List<PlayerStatsDto>> getAcwrRanking() {
        return ResponseEntity.ok(performanceService.getAcwrRanking());
    }

    // ══════════════════════════════════════════════════════════════════════
    //  NOUVEAUX ENDPOINTS — ANOMALIES
    // ══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/performance/anomaly/{playerId}
     * Analyse d'anomalie Z-Score + EWMA pour un joueur spécifique.
     * Retourne : type, sévérité, zScore, ewmaScore, ewmaDrop, message.
     */
    @GetMapping("/anomaly/{playerId}")
    public ResponseEntity<PlayerStatsDto> getAnomalyStats(@PathVariable Long playerId) {
        return performanceService.getAnomalyStats(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/performance/anomaly/alerts
     * Liste tous les joueurs avec des anomalies actives (MEDIUM, HIGH, CRITICAL).
     * Triés par sévérité décroissante — endpoint principal du dashboard coach.
     */
    @GetMapping("/anomaly/alerts")
    public ResponseEntity<List<PlayerStatsDto>> getAnomalyAlerts() {
        return ResponseEntity.ok(performanceService.getPlayersWithAnomalies());
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ENDPOINT COMBINÉ — ACWR + Anomalie + Stats complètes
    // ══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/performance/full-analysis/{playerId}
     * Analyse complète : ACWR + Anomalie + toutes les métriques existantes.
     * Endpoint principal pour la vue détaillée d'un joueur côté coach.
     */
    @GetMapping("/full-analysis/{playerId}")
    public ResponseEntity<PlayerStatsDto> getFullAnalysis(@PathVariable Long playerId) {
        return performanceService.getFullAnalysis(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
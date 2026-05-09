package com.example.streetleague.Controller;

import com.example.streetleague.ServiceImp.StatsService;
import com.example.streetleague.dto.DashboardStatsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class StatsController {

    private final StatsService statsService;

    /**
     * GET /api/stats/dashboard?jours=30
     * Retourne toutes les stats du dashboard en un seul appel.
     * @param jours  période en jours (défaut 30)
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboard(
            @RequestParam(defaultValue = "30") int jours) {
        return ResponseEntity.ok(statsService.getDashboard(jours));
    }
}
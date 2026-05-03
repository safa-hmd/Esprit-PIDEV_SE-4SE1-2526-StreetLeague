package com.example.streetleague.Controller;

import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.ServiceInterface.ImatchHistoryService;
import com.example.streetleague.dto.MatchHistoryDto;
import com.example.streetleague.dto.MatchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("matches-history")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://streetleaguefrontend.azurewebsites.net"
})
@RequiredArgsConstructor
public class MatchHistoryController {

    private final ImatchHistoryService matchHistoryService;

    // JPQL : filtres optionnels status + période
    @GetMapping("/enriched")
    public ResponseEntity<List<MatchHistoryDto>> getEnrichedHistory(
            @RequestParam(required = false) MatchStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(
                matchHistoryService.getMatchHistory(status, from, to)
        );
    }

    // Keywords : recherche par nom d'équipe + statuts
    @GetMapping("/search")
    public ResponseEntity<List<MatchResponse>> searchMatches(
            @RequestParam(defaultValue = "") String teamName,
            @RequestParam(defaultValue = "PENDING,ACCEPTED,FINISHED,CANCELLED,REJECTED")
            List<String> statuses
    ) {
        return ResponseEntity.ok(
                matchHistoryService.searchMatchesByTeamAndStatus(teamName, statuses)
        );
    }
}
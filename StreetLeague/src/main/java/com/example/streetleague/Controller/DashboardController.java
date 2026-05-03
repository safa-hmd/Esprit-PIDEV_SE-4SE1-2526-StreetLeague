package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.DashboardService;
import com.example.streetleague.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://streetleaguefrontend.azurewebsites.net"
})
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<FinancialSummaryDto> getSummary() {
        return ResponseEntity.ok(dashboardService.getFinancialSummary());
    }

    @GetMapping("/revenue/fields")
    public ResponseEntity<List<RevenueByFieldDto>> getRevenueByField() {
        return ResponseEntity.ok(dashboardService.getRevenueByField());
    }

    @GetMapping("/revenue/sports")
    public ResponseEntity<List<RevenueBySportDto>> getRevenueBySport() {
        return ResponseEntity.ok(dashboardService.getRevenueBySport());
    }

    @GetMapping("/revenue/months")
    public ResponseEntity<List<RevenueByMonthDto>> getRevenueByMonth() {
        return ResponseEntity.ok(dashboardService.getRevenueByMonth());
    }

    @GetMapping("/top-players")
    public ResponseEntity<List<TopPlayerDto>> getTopPlayers() {
        return ResponseEntity.ok(dashboardService.getTopPlayers());
    }
}
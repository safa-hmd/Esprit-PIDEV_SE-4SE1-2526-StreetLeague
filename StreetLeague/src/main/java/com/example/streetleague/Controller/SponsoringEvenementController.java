package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.SponsoringEvenementService;
import com.example.streetleague.dto.CommunauteStatsDTO;
import com.example.streetleague.dto.DashboardSponsorCommunauteDTO;
import com.example.streetleague.dto.SponsoringEvenementDTO;
import com.example.streetleague.dto.TopCommunauteDTO;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sponsoring")
public class SponsoringEvenementController {
    private final SponsoringEvenementService service;

    public SponsoringEvenementController(SponsoringEvenementService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<SponsoringEvenementDTO> create(@Valid @RequestBody SponsoringEvenementDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SponsoringEvenementDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SponsoringEvenementDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<SponsoringEvenementDTO> update(@PathVariable Long id, @Valid @RequestBody SponsoringEvenementDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SponsoringEvenementDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statut = request.get("statut");
        SponsoringEvenementDTO updated = service.updateStatus(id, statut);
        
        // Si le sponsoring a été supprimé (rejeté)
        if (updated == null) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        
        return ResponseEntity.ok(updated); // 200 OK avec le sponsoring approuvé
    }


    
    // Endpoint de test pour voir toutes les sponsoring_evenement (sans filtres)
    @GetMapping("/test/all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<SponsoringEvenementDTO>> getAllTest() {
        return ResponseEntity.ok(service.getAll());
    }

    // Endpoint de test pour mettre à jour le statut (sans authentification)
    @PatchMapping("/test/{id}/status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<SponsoringEvenementDTO> updateStatusTest(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statut = request.get("statut");
        SponsoringEvenementDTO updated = service.updateStatus(id, statut);
        
        if (updated == null) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(updated);
    }

    // ===== MÉTIERS AVANCÉS : JOINTURES 3+ TABLES =====

    @GetMapping("/stats/communaute")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR', 'ROLE_COMMUNITY_MANAGER')")
    public ResponseEntity<List<CommunauteStatsDTO>> getContributionTotaleParCommunaute() {
        return ResponseEntity.ok(service.getContributionTotaleParCommunaute());
    }

    @GetMapping("/stats/top-communautes")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR', 'ROLE_COMMUNITY_MANAGER')")
    public ResponseEntity<List<TopCommunauteDTO>> getTopCommunautesAvecSponsorings(
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "1") Long seuilMinimum) {
        return ResponseEntity.ok(service.getTopCommunautesAvecSponsorings(statut, seuilMinimum));
    }

    @GetMapping("/stats/dashboard-sponsor")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<List<DashboardSponsorCommunauteDTO>> getDashboardSponsorParCommunaute(
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(service.getDashboardSponsorParCommunaute(statut));
    }

    // Endpoints de test (sans authentification)
    @GetMapping("/test/stats/communaute")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CommunauteStatsDTO>> getContributionTotaleParCommunauteTest() {
        return ResponseEntity.ok(service.getContributionTotaleParCommunaute());
    }

    @GetMapping("/test/stats/top-communautes")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<TopCommunauteDTO>> getTopCommunautesAvecSponsoringsTest(
            @RequestParam(required = false) String statut,
            @RequestParam(defaultValue = "1") Long seuilMinimum) {
        return ResponseEntity.ok(service.getTopCommunautesAvecSponsorings(statut, seuilMinimum));
    }

    @GetMapping("/test/stats/dashboard-sponsor")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<DashboardSponsorCommunauteDTO>> getDashboardSponsorParCommunauteTest(
            @RequestParam(required = false) String statut) {
        return ResponseEntity.ok(service.getDashboardSponsorParCommunaute(statut));
    }
}

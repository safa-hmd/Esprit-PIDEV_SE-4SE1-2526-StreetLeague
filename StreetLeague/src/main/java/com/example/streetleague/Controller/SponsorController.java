package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.SponsorService;
import com.example.streetleague.dto.ComparaisonSponsorDTO;
import com.example.streetleague.dto.SponsorDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sponsor")
public class SponsorController {
    private final SponsorService service;

    public SponsorController(SponsorService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<SponsorDTO> create(@Valid @RequestBody SponsorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SponsorDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SponsorDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<SponsorDTO> update(@PathVariable Long id, @Valid @RequestBody SponsorDTO dto) {
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
    public ResponseEntity<SponsorDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statut = request.get("statut");
        SponsorDTO updated = service.updateStatus(id, statut);
        
        // Si le sponsor a été supprimé (rejeté)
        if (updated == null) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        
        return ResponseEntity.ok(updated); // 200 OK avec le sponsor approuvé
    }

    // ===== MÉTIER AVANCÉ : SOUS-REQUÊTES CORRÉLÉES 3 TABLES =====

    @GetMapping("/stats/comparaison-contrats")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<List<ComparaisonSponsorDTO>> getComparaisonContratsVsSponsorings() {
        return ResponseEntity.ok(service.getComparaisonContratsVsSponsorings());
    }

    // Endpoint de test (sans authentification)
    @GetMapping("/test/stats/comparaison-contrats")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ComparaisonSponsorDTO>> getComparaisonContratsVsSponsoringsTest() {
        return ResponseEntity.ok(service.getComparaisonContratsVsSponsorings());
    }
}


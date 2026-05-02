package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.EvenementCommunauteService;
import com.example.streetleague.dto.EvenementCommunauteDTO;
import com.example.streetleague.dto.EvenementSansSponsoringDTO;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import com.example.streetleague.Scheduler.EventAlertScheduler;

@RestController
@RequestMapping("/api/evenement")
public class EvenementCommunauteController {
    private final EvenementCommunauteService service;
    private final EventAlertScheduler alertScheduler;

    public EvenementCommunauteController(EvenementCommunauteService service, EventAlertScheduler alertScheduler) {
        this.service = service;
        this.alertScheduler = alertScheduler;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_COMMUNITY_MANAGER')")
    public ResponseEntity<EvenementCommunauteDTO> create(@Valid @RequestBody EvenementCommunauteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<EvenementCommunauteDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvenementCommunauteDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_COMMUNITY_MANAGER')")
    public ResponseEntity<EvenementCommunauteDTO> update(@PathVariable Long id, @Valid @RequestBody EvenementCommunauteDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_COMMUNITY_MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== MÉTIER AVANCÉ : LEFT JOIN 3 TABLES =====

    @GetMapping("/stats/sans-sponsoring")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_COMMUNITY_MANAGER', 'ROLE_SPONSOR')")
    public ResponseEntity<List<EvenementSansSponsoringDTO>> getEvenementsSansSponsoring() {
        return ResponseEntity.ok(service.getEvenementsSansSponsoring());
    }

    @GetMapping("/stats/sans-sponsoring/apres-date")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_COMMUNITY_MANAGER', 'ROLE_SPONSOR')")
    public ResponseEntity<List<EvenementSansSponsoringDTO>> getEvenementsSansSponsoringApresDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut) {
        return ResponseEntity.ok(service.getEvenementsSansSponsoringApresDate(dateDebut));
    }

    // Endpoints de test (sans authentification)
    @GetMapping("/test/stats/sans-sponsoring")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<EvenementSansSponsoringDTO>> getEvenementsSansSponsoringTest() {
        return ResponseEntity.ok(service.getEvenementsSansSponsoring());
    }

    @GetMapping("/test/stats/sans-sponsoring/apres-date")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<EvenementSansSponsoringDTO>> getEvenementsSansSponsoringApresDateTest(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut) {
        return ResponseEntity.ok(service.getEvenementsSansSponsoringApresDate(dateDebut));
    }

    // Endpoint pour déclencher manuellement l'envoi de l'alerte email
    @GetMapping("/test/trigger-alert")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> triggerEmailAlert() {
        alertScheduler.checkForOrphanEvents();
        return ResponseEntity.ok("Vérification déclenchée manuellement. Si des événements sont orphelins, un email a été envoyé.");
    }
}


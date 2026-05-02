package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.ContratSponsorService;
import com.example.streetleague.dto.ContratSponsorDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/contrat", "/api/contrat-sponsor"})
public class ContratSponsorController {
    private final ContratSponsorService service;

    public ContratSponsorController(ContratSponsorService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<ContratSponsorDTO> create(@Valid @RequestBody ContratSponsorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ContratSponsorDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratSponsorDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SPONSOR')")
    public ResponseEntity<ContratSponsorDTO> update(@PathVariable Long id, @Valid @RequestBody ContratSponsorDTO dto) {
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
    public ResponseEntity<ContratSponsorDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statut = request.get("statut");
        ContratSponsorDTO updated = service.updateStatus(id, statut);
        
        // Si le contrat a été supprimé (rejeté)
        if (updated == null) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        
        return ResponseEntity.ok(updated); // 200 OK avec le contrat approuvé
    }
}


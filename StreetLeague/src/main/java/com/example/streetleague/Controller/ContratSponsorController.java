package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.ContratSponsorService;
import com.example.streetleague.dto.ContratSponsorDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/contrat", "/api/contrat-sponsor"})
public class ContratSponsorController {
    private final ContratSponsorService service;

    public ContratSponsorController(ContratSponsorService service) {
        this.service = service;
    }

    @PostMapping
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
    public ResponseEntity<ContratSponsorDTO> update(@PathVariable Long id, @Valid @RequestBody ContratSponsorDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


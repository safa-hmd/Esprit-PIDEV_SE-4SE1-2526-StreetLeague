package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.SponsorService;
import com.example.streetleague.dto.SponsorDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sponsor")
public class SponsorController {
    private final SponsorService service;

    public SponsorController(SponsorService service) {
        this.service = service;
    }

    @PostMapping
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
    public ResponseEntity<SponsorDTO> update(@PathVariable Long id, @Valid @RequestBody SponsorDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


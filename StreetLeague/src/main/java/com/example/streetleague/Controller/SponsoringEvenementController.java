package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.SponsoringEvenementService;
import com.example.streetleague.dto.SponsoringEvenementDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sponsoring")
public class SponsoringEvenementController {
    private final SponsoringEvenementService service;

    public SponsoringEvenementController(SponsoringEvenementService service) {
        this.service = service;
    }

    @PostMapping
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
    public ResponseEntity<SponsoringEvenementDTO> update(@PathVariable Long id, @Valid @RequestBody SponsoringEvenementDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

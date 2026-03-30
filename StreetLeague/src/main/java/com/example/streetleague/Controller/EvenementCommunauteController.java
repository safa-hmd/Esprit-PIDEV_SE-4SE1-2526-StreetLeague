package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.EvenementCommunauteService;
import com.example.streetleague.dto.EvenementCommunauteDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evenement")
public class EvenementCommunauteController {
    private final EvenementCommunauteService service;

    public EvenementCommunauteController(EvenementCommunauteService service) {
        this.service = service;
    }

    @PostMapping
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
    public ResponseEntity<EvenementCommunauteDTO> update(@PathVariable Long id, @Valid @RequestBody EvenementCommunauteDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.CommunauteService;
import com.example.streetleague.dto.CommunauteDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communaute")
public class CommunauteController {
    private final CommunauteService service;

    public CommunauteController(CommunauteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CommunauteDTO> create(@Valid @RequestBody CommunauteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CommunauteDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunauteDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunauteDTO> update(@PathVariable Long id, @Valid @RequestBody CommunauteDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

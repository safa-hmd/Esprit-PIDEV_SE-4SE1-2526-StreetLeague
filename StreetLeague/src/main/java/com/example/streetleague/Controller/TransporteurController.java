package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.TransporteurService;
import com.example.streetleague.domain.Transporteur;
import com.example.streetleague.dto.TransporteurDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transporteurs")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TransporteurController {

    private final TransporteurService transporteurService;

    @PostMapping
    public ResponseEntity<Transporteur> create(@Valid @RequestBody TransporteurDTO dto) {
        return ResponseEntity.ok(transporteurService.createTransporteur(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transporteur> update(@PathVariable Long id, @Valid @RequestBody TransporteurDTO dto) {
        return ResponseEntity.ok(transporteurService.updateTransporteur(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        transporteurService.deleteTransporteur(id);
        return ResponseEntity.ok("Transporteur supprimé avec succès");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transporteur> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transporteurService.getTransporteurById(id));
    }

    @GetMapping
    public ResponseEntity<List<Transporteur>> getAll() {
        return ResponseEntity.ok(transporteurService.getAllTransporteurs());
    }
}
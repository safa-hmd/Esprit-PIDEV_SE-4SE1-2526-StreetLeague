package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.LivraisonService;
import com.example.streetleague.domain.Livraison;
import com.example.streetleague.dto.LivraisonDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livraisons")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LivraisonController {

    private final LivraisonService livraisonService;

    @PostMapping
    public ResponseEntity<Livraison> create(@Valid @RequestBody LivraisonDTO dto) {
        return ResponseEntity.ok(livraisonService.createLivraison(dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Livraison> updateStatus(@PathVariable Long id, @Valid @RequestBody LivraisonDTO dto) {
        return ResponseEntity.ok(livraisonService.updateLivraisonStatus(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livraison> getById(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.getLivraisonById(id));
    }

    @GetMapping
    public ResponseEntity<List<Livraison>> getAll() {
        return ResponseEntity.ok(livraisonService.getAllLivraisons());
    }
}
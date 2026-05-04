package com.example.streetleague.Controller;

import com.example.streetleague.ServiceImp.TspService;
import com.example.streetleague.ServiceInterface.LivraisonService;
import com.example.streetleague.domain.LivraisonStatus;
import com.example.streetleague.dto.LivraisonDTO;
import com.example.streetleague.dto.LivraisonResponseDTO;
import com.example.streetleague.dto.TourneeDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // important pour le frontend (Leaflet / Angular / React)
public class LivraisonController {

    private final LivraisonService livraisonService;
    private final TspService tspService; // ⭐ service TSP injecté

    // =============================
    // CRUD LIVRAISONS
    // =============================

    @PostMapping
    public ResponseEntity<LivraisonResponseDTO> create(@Valid @RequestBody LivraisonDTO dto) {
        log.info("Création livraison");
        return ResponseEntity.ok(
                LivraisonResponseDTO.from(livraisonService.createLivraison(dto))
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LivraisonResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody LivraisonDTO dto) {

        log.info("Update statut livraison {}", id);
        return ResponseEntity.ok(
                LivraisonResponseDTO.from(livraisonService.updateLivraisonStatus(id, dto))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivraisonResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                LivraisonResponseDTO.from(livraisonService.getLivraisonById(id))
        );
    }

    @GetMapping
    public ResponseEntity<List<LivraisonResponseDTO>> getAll() {
        return ResponseEntity.ok(
                livraisonService.getAllLivraisons()
                        .stream()
                        .map(LivraisonResponseDTO::from)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<LivraisonResponseDTO>> getByStatut(
            @PathVariable LivraisonStatus statut) {

        return ResponseEntity.ok(
                livraisonService.getLivraisonsByStatut(statut)
                        .stream()
                        .map(LivraisonResponseDTO::from)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/livreur/{livreurId}")
    public ResponseEntity<List<LivraisonResponseDTO>> getByLivreur(
            @PathVariable Long livreurId) {

        return ResponseEntity.ok(
                livraisonService.getLivraisonsByLivreur(livreurId)
                        .stream()
                        .map(LivraisonResponseDTO::from)
                        .collect(Collectors.toList())
        );
    }

    // =========================================================
    // 🚚🚚🚚  ENDPOINT IA LOGISTIQUE — ROUTE OPTIMISÉE (TSP)
    // =========================================================
    //
    // URL finale pour le frontend :
    //
    // http://localhost:8086/api/livraisons/tournee/{livreurId}
    //
    // Cette API retourne :
    // - ordre des livraisons optimisé
    // - distance totale
    // - ETA totale
    // - polylines OSRM pour affichage map
    //
    // =========================================================
    @GetMapping("/tournee/{livreurId}")
    public ResponseEntity<TourneeDTO> getTournee(@PathVariable Long livreurId) {

        log.info("📍 Calcul tournée optimisée pour livreur {}", livreurId);

        TourneeDTO tournee = tspService.calculerTournee(livreurId);

        log.info("✅ Tournée calculée : {} stops | {} km | {} min",
                tournee.getStops().size(),
                tournee.getDistanceTotaleKm(),
                tournee.getEtaTotalMinutes());

        return ResponseEntity.ok(tournee);
    }
}
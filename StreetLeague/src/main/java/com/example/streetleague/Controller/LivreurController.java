package com.example.streetleague.Controller;

import com.example.streetleague.Repository.LivraisonRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.LocationDTO;
import com.example.streetleague.dto.StatsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class LivreurController {

    private final UserRepository userRepository;
    private final LivraisonRepository livraisonRepository;

    // ════════════════════════════════════════════════════
    // PARTIE 3 — Tracking GPS livreur
    // PUT /api/livreurs/location
    // ════════════════════════════════════════════════════
    @PutMapping("/api/livreurs/location")
    public ResponseEntity<String> updateLocation(
            @RequestBody LocationDTO dto,
            Authentication auth) {

        String email = auth.getName();
        User livreur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        livreur.setLatitude(dto.getLatitude());
        livreur.setLongitude(dto.getLongitude());

        // Si OFFLINE → passe DISPONIBLE au premier ping GPS
        if (livreur.getStatusLivreur() == LivreurStatus.OFFLINE) {
            livreur.setStatusLivreur(LivreurStatus.DISPONIBLE);
        }

        userRepository.save(livreur);
        return ResponseEntity.ok("Position mise à jour");
    }

    // ── Changer statut livreur manuellement ───────────────
    @PutMapping("/api/livreurs/status")
    public ResponseEntity<String> updateStatus(
            @RequestParam LivreurStatus status,
            Authentication auth) {

        String email = auth.getName();
        User livreur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        livreur.setStatusLivreur(status);
        userRepository.save(livreur);
        return ResponseEntity.ok("Statut mis à jour : " + status);
    }

    // ════════════════════════════════════════════════════
    // PARTIE 8 — Admin Dispatch Dashboard
    // ════════════════════════════════════════════════════

    // GET /api/admin/livraisons/pending
    @GetMapping("/api/admin/livraisons/pending")
    public ResponseEntity<List<Livraison>> getPending() {
        return ResponseEntity.ok(
                livraisonRepository.findByStatut(LivraisonStatus.PREPAREE)
        );
    }

    // GET /api/admin/livraisons/en-cours
    @GetMapping("/api/admin/livraisons/en-cours")
    public ResponseEntity<List<Livraison>> getEnCours() {
        List<Livraison> enCours = livraisonRepository.findAll().stream()
                .filter(l -> l.getStatut() == LivraisonStatus.ASSIGNEE
                        || l.getStatut() == LivraisonStatus.EXPEDIEE
                        || l.getStatut() == LivraisonStatus.OUT_FOR_DELIVERY)
                .toList();
        return ResponseEntity.ok(enCours);
    }

    // GET /api/admin/stats
    @GetMapping("/api/admin/stats")
    public ResponseEntity<StatsDTO> getStats() {
        List<Livraison> toutes = livraisonRepository.findAll();
        long total     = toutes.size();
        long livrees   = livraisonRepository.countByStatut(LivraisonStatus.LIVREE);
        long enCours   = livraisonRepository.countByStatut(LivraisonStatus.ASSIGNEE)
                + livraisonRepository.countByStatut(LivraisonStatus.EXPEDIEE)
                + livraisonRepository.countByStatut(LivraisonStatus.OUT_FOR_DELIVERY);

        long disponibles = userRepository
                .findByRoleAndStatusLivreur(Role.DELIVERY, LivreurStatus.DISPONIBLE)
                .size();

        // Livreur le plus actif (le plus de livraisons en cours)
        String livreurActif = userRepository.findByRole(Role.DELIVERY).stream()
                .max((a, b) -> Integer.compare(a.getLivraisonsEnCours(), b.getLivraisonsEnCours()))
                .map(User::getFullName)
                .orElse("Aucun");

        double taux = total > 0 ? (double) livrees / total * 100 : 0;

        return ResponseEntity.ok(StatsDTO.builder()
                .nbLivraisonsEnCours(enCours)
                .nbLivreursDisponibles(disponibles)
                .livreurLePlusActif(livreurActif)
                .tauxLivraisonsReussies(Math.round(taux * 100.0) / 100.0)
                .build());
    }
}
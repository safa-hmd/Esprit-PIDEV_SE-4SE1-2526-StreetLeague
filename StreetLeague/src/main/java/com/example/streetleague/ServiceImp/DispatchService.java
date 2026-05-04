package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.LivraisonRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

    private final UserRepository userRepository;
    private final LivraisonRepository livraisonRepository;

    // ════════════════════════════════════════════════════
    // PARTIE 4 — Calcul distance Haversine (en KM)
    // ════════════════════════════════════════════════════
    public double calculDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    // ════════════════════════════════════════════════════
    // PARTIE 5 — Algorithme de scoring
    // ✅ Null-safe : si GPS null → utilise 0.0 par défaut
    // ════════════════════════════════════════════════════
    public double calculScore(User livreur, Livraison livraison) {
        // ✅ Protection null GPS livreur
        double latLivreur  = livreur.getLatitude()  != null ? livreur.getLatitude()  : 0.0;
        double lonLivreur  = livreur.getLongitude() != null ? livreur.getLongitude() : 0.0;

        // ✅ Protection null GPS livraison (anciennes données sans GPS)
        double latClient   = livraison.getLatitudeClient()  != null ? livraison.getLatitudeClient()  : 0.0;
        double lonClient   = livraison.getLongitudeClient() != null ? livraison.getLongitudeClient() : 0.0;

        // 1. Distance GPS
        double distance = calculDistance(latLivreur, lonLivreur, latClient, lonClient);

        // 2. Livraisons en cours
        int livraisonsEnCours = livreur.getLivraisonsEnCours();

        // 3. Score disponibilité
        double scoreDisponibilite;
        if (livreur.getStatusLivreur() == null) {
            scoreDisponibilite = 100; // inconnu → exclu
        } else {
            switch (livreur.getStatusLivreur()) {
                case DISPONIBLE -> scoreDisponibilite = 0;
                case OCCUPE     -> scoreDisponibilite = 5;
                default         -> scoreDisponibilite = 100; // OFFLINE → exclu
            }
        }

        // 4. Score final
        return (distance * 0.5)
                + (livraisonsEnCours * 0.3)
                + (scoreDisponibilite * 0.2);
    }

    // ════════════════════════════════════════════════════
    // PARTIE 6 — Auto-assignation du meilleur livreur
    // ════════════════════════════════════════════════════
    public Optional<User> autoAssignerLivreur(Livraison livraison) {
        List<User> livreurs = userRepository.findByRole(Role.DELIVERY);

        // Garder seulement les livreurs non OFFLINE
        List<User> livreurActifs = livreurs.stream()
                .filter(l -> l.getStatusLivreur() != null
                        && l.getStatusLivreur() != LivreurStatus.OFFLINE)
                .toList();

        if (livreurActifs.isEmpty()) {
            log.warn("Aucun livreur disponible pour la livraison #{}", livraison.getId());
            return Optional.empty();
        }

        // Choisir le livreur avec le score minimal
        User meilleurLivreur = livreurActifs.stream()
                .min(Comparator.comparingDouble(l -> calculScore(l, livraison)))
                .orElse(null);

        if (meilleurLivreur == null) return Optional.empty();

        double score    = calculScore(meilleurLivreur, livraison);
        double latLiv   = meilleurLivreur.getLatitude()  != null ? meilleurLivreur.getLatitude()  : 0.0;
        double lonLiv   = meilleurLivreur.getLongitude() != null ? meilleurLivreur.getLongitude() : 0.0;
        double latCli   = livraison.getLatitudeClient()  != null ? livraison.getLatitudeClient()  : 0.0;
        double lonCli   = livraison.getLongitudeClient() != null ? livraison.getLongitudeClient() : 0.0;
        double distance = calculDistance(latLiv, lonLiv, latCli, lonCli);

        // Assigner le livreur à la livraison
        livraison.setLivreur(meilleurLivreur);
        livraison.setStatut(LivraisonStatus.ASSIGNEE);
        livraison.setDateAffectation(LocalDateTime.now());
        livraison.setScoreAffectation(score);
        livraison.setDistance(distance);
        livraisonRepository.save(livraison);

        // Mettre à jour le livreur
        meilleurLivreur.setLivraisonsEnCours(meilleurLivreur.getLivraisonsEnCours() + 1);
        meilleurLivreur.setStatusLivreur(LivreurStatus.OCCUPE);
        userRepository.save(meilleurLivreur);

        log.info("✅ Livraison #{} assignée à {} (score={}, distance={}km)",
                livraison.getId(), meilleurLivreur.getFullName(),
                String.format("%.2f", score), String.format("%.2f", distance));

        return Optional.of(meilleurLivreur);
    }
}
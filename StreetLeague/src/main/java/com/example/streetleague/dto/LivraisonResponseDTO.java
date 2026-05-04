package com.example.streetleague.dto;

import com.example.streetleague.domain.Livraison;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivraisonResponseDTO {

    private Long id;
    private Long commandeId;

    // Livreur aplati (pas d'objet nested complet)
    private Long livreurId;
    private String livreurNom;
    private String livreurEmail;

    private String adresse;
    private Double latitudeClient;
    private Double longitudeClient;

    private LocalDateTime dateCreation;
    private LocalDateTime dateAffectation;
    private LocalDateTime dateLivraison;

    private double fraisLivraison;
    private Double distance;
    private String priorite;
    private Double scoreAffectation;
    private String statut;
    private int nbTentatives;
    private String motifEchec;

    // ── Factory method ────────────────────────────────────
    public static LivraisonResponseDTO from(Livraison l) {
        return LivraisonResponseDTO.builder()
                .id(l.getId())
                .commandeId(l.getCommande() != null ? l.getCommande().getId() : null)
                .livreurId(l.getLivreur() != null ? l.getLivreur().getId() : null)
                .livreurNom(l.getLivreur() != null ? l.getLivreur().getFullName() : null)
                .livreurEmail(l.getLivreur() != null ? l.getLivreur().getEmail() : null)
                .adresse(l.getAdresse())
                .latitudeClient(l.getLatitudeClient())
                .longitudeClient(l.getLongitudeClient())
                .dateCreation(l.getDateCreation())
                .dateAffectation(l.getDateAffectation())
                .dateLivraison(l.getDateLivraison())
                .fraisLivraison(l.getFraisLivraison())
                .distance(l.getDistance())
                .priorite(l.getPriorite() != null ? l.getPriorite().name() : "NORMAL")
                .scoreAffectation(l.getScoreAffectation())
                .statut(l.getStatut() != null ? l.getStatut().name() : null)
                .nbTentatives(l.getNbTentatives())
                .motifEchec(l.getMotifEchec())
                .build();
    }
}
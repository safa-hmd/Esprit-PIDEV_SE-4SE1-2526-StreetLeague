package com.example.streetleague.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourneeDTO {

    private Long    livreurId;
    private String  livreurNom;
    private Double  latDepart;
    private Double  lonDepart;
    private List<StopDTO> stops;
    private double  distanceTotaleKm;
    private int     etaTotalMinutes;
    private List<String> polylines;   // ⬅️ AJOUT : liste des polylines encodées OSRM

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StopDTO {
        private int    ordre;
        private Long   livraisonId;
        private Long   commandeId;
        private String adresse;
        private double latitude;
        private double longitude;
        private String statut;
        private double distanceDepuisPrecedentKm;
        private int    etaMinutesDepuisDepart;
        private double fraisLivraison;
    }
}
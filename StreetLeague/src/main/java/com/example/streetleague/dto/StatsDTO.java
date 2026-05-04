package com.example.streetleague.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDTO {
    private long nbLivraisonsEnCours;
    private long nbLivreursDisponibles;
    private String livreurLePlusActif;
    private double tauxLivraisonsReussies;
}
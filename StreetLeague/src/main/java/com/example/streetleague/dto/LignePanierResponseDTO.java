package com.example.streetleague.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LignePanierResponseDTO {

    private Long ligneId;
    private Long materielId;
    private String materielNom;
    private double prix;
    private int quantite;
    private double sousTotal;
}
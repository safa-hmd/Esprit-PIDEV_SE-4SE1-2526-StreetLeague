package com.example.streetleague.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CartItemDTO {
    private Long materielId;
    private String nom;
    private Double prixUnitaire;
    private Integer quantite;
    private Long categorieId;
}
package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.*;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
public class AddToCartDTO {

    @NotNull(message = "User obligatoire")
    private Long userId;

    @NotNull(message = "Materiel obligatoire")
    private Long materielId;

    @Min(value = 1, message = "Quantité doit être >= 1")
    private int quantite;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMaterielId() {
        return materielId;
    }

    public void setMaterielId(Long materielId) {
        this.materielId = materielId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public AddToCartDTO(Long userId, Long materielId, int quantite) {
        this.userId = userId;
        this.materielId = materielId;
        this.quantite = quantite;
    }

    public AddToCartDTO() {

    }
}
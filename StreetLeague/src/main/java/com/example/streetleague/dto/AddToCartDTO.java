package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import java.util.Objects;

public class AddToCartDTO {

    @NotNull(message = "User obligatoire")
    private Long userId;

    @NotNull(message = "Materiel obligatoire")
    private Long materielId;

    @Min(value = 1, message = "Quantité doit être >= 1")
    private int quantite;

    public AddToCartDTO() {
    }

    public AddToCartDTO(Long userId, Long materielId, int quantite) {
        this.userId = userId;
        this.materielId = materielId;
        this.quantite = quantite;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddToCartDTO that = (AddToCartDTO) o;
        return quantite == that.quantite && Objects.equals(userId, that.userId) && Objects.equals(materielId, that.materielId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, materielId, quantite);
    }

    @Override
    public String toString() {
        return "AddToCartDTO{" +
                "userId=" + userId +
                ", materielId=" + materielId +
                ", quantite=" + quantite +
                '}';
    }

    public static AddToCartDTOBuilder builder() {
        return new AddToCartDTOBuilder();
    }

    public static class AddToCartDTOBuilder {
        private Long userId;
        private Long materielId;
        private int quantite;

        public AddToCartDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public AddToCartDTOBuilder materielId(Long materielId) { this.materielId = materielId; return this; }
        public AddToCartDTOBuilder quantite(int quantite) { this.quantite = quantite; return this; }

        public AddToCartDTO build() {
            return new AddToCartDTO(userId, materielId, quantite);
        }
    }
}
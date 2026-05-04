package com.example.streetleague.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class LigneCommandeDTO {

    @NotNull(message = "Materiel est obligatoire")
    private Long materielId;

    @Min(value = 1, message = "Quantité doit être >= 1")
    private int quantite;

    public LigneCommandeDTO() {
    }

    public LigneCommandeDTO(Long materielId, int quantite) {
        this.materielId = materielId;
        this.quantite = quantite;
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
        LigneCommandeDTO that = (LigneCommandeDTO) o;
        return quantite == that.quantite && Objects.equals(materielId, that.materielId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materielId, quantite);
    }

    @Override
    public String toString() {
        return "LigneCommandeDTO{" +
                "materielId=" + materielId +
                ", quantite=" + quantite +
                '}';
    }

    public static LigneCommandeDTOBuilder builder() {
        return new LigneCommandeDTOBuilder();
    }

    public static class LigneCommandeDTOBuilder {
        private Long materielId;
        private int quantite;

        public LigneCommandeDTOBuilder materielId(Long materielId) { this.materielId = materielId; return this; }
        public LigneCommandeDTOBuilder quantite(int quantite) { this.quantite = quantite; return this; }

        public LigneCommandeDTO build() {
            return new LigneCommandeDTO(materielId, quantite);
        }
    }
}
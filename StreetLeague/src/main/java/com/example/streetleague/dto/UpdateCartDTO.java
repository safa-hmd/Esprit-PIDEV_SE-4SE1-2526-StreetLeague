package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import java.util.Objects;

public class UpdateCartDTO {

    @NotNull
    private Long lignePanierId;

    @Min(value = 1, message = "Quantité >= 1")
    private int quantite;

    public UpdateCartDTO() {
    }

    public UpdateCartDTO(Long lignePanierId, int quantite) {
        this.lignePanierId = lignePanierId;
        this.quantite = quantite;
    }

    public Long getLignePanierId() {
        return lignePanierId;
    }

    public void setLignePanierId(Long lignePanierId) {
        this.lignePanierId = lignePanierId;
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
        UpdateCartDTO that = (UpdateCartDTO) o;
        return quantite == that.quantite && Objects.equals(lignePanierId, that.lignePanierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lignePanierId, quantite);
    }

    @Override
    public String toString() {
        return "UpdateCartDTO{" +
                "lignePanierId=" + lignePanierId +
                ", quantite=" + quantite +
                '}';
    }

    public static UpdateCartDTOBuilder builder() {
        return new UpdateCartDTOBuilder();
    }

    public static class UpdateCartDTOBuilder {
        private Long lignePanierId;
        private int quantite;

        public UpdateCartDTOBuilder lignePanierId(Long lignePanierId) { this.lignePanierId = lignePanierId; return this; }
        public UpdateCartDTOBuilder quantite(int quantite) { this.quantite = quantite; return this; }

        public UpdateCartDTO build() {
            return new UpdateCartDTO(lignePanierId, quantite);
        }
    }
}
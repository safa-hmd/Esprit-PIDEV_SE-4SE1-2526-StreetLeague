package com.example.streetleague.dto;

import java.util.List;
import java.util.Objects;

public class PanierResponseDTO {

    private Long panierId;
    private Long userId;
    private List<LignePanierResponseDTO> lignes;
    private double total;

    public PanierResponseDTO() {
    }

    public PanierResponseDTO(Long panierId, Long userId, List<LignePanierResponseDTO> lignes, double total) {
        this.panierId = panierId;
        this.userId = userId;
        this.lignes = lignes;
        this.total = total;
    }

    public Long getPanierId() {
        return panierId;
    }

    public void setPanierId(Long panierId) {
        this.panierId = panierId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<LignePanierResponseDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<LignePanierResponseDTO> lignes) {
        this.lignes = lignes;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PanierResponseDTO that = (PanierResponseDTO) o;
        return Double.compare(that.total, total) == 0 && Objects.equals(panierId, that.panierId) && Objects.equals(userId, that.userId) && Objects.equals(lignes, that.lignes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(panierId, userId, lignes, total);
    }

    @Override
    public String toString() {
        return "PanierResponseDTO{" +
                "panierId=" + panierId +
                ", userId=" + userId +
                ", total=" + total +
                '}';
    }

    public static PanierResponseDTOBuilder builder() {
        return new PanierResponseDTOBuilder();
    }

    public static class PanierResponseDTOBuilder {
        private Long panierId;
        private Long userId;
        private List<LignePanierResponseDTO> lignes;
        private double total;

        public PanierResponseDTOBuilder panierId(Long panierId) { this.panierId = panierId; return this; }
        public PanierResponseDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public PanierResponseDTOBuilder lignes(List<LignePanierResponseDTO> lignes) { this.lignes = lignes; return this; }
        public PanierResponseDTOBuilder total(double total) { this.total = total; return this; }

        public PanierResponseDTO build() {
            return new PanierResponseDTO(panierId, userId, lignes, total);
        }
    }
}
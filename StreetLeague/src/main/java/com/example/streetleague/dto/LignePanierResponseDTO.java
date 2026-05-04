package com.example.streetleague.dto;

import java.util.Objects;

public class LignePanierResponseDTO {

    private Long ligneId;
    private Long materielId;
    private String materielNom;
    private double prix;
    private int quantite;
    private double sousTotal;

    public LignePanierResponseDTO() {
    }

    public LignePanierResponseDTO(Long ligneId, Long materielId, String materielNom, double prix, int quantite, double sousTotal) {
        this.ligneId = ligneId;
        this.materielId = materielId;
        this.materielNom = materielNom;
        this.prix = prix;
        this.quantite = quantite;
        this.sousTotal = sousTotal;
    }

    public Long getLigneId() {
        return ligneId;
    }

    public void setLigneId(Long ligneId) {
        this.ligneId = ligneId;
    }

    public Long getMaterielId() {
        return materielId;
    }

    public void setMaterielId(Long materielId) {
        this.materielId = materielId;
    }

    public String getMaterielNom() {
        return materielNom;
    }

    public void setMaterielNom(String materielNom) {
        this.materielNom = materielNom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getSousTotal() {
        return sousTotal;
    }

    public void setSousTotal(double sousTotal) {
        this.sousTotal = sousTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LignePanierResponseDTO that = (LignePanierResponseDTO) o;
        return Double.compare(that.prix, prix) == 0 && quantite == that.quantite && Double.compare(that.sousTotal, sousTotal) == 0 && Objects.equals(ligneId, that.ligneId) && Objects.equals(materielId, that.materielId) && Objects.equals(materielNom, that.materielNom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ligneId, materielId, materielNom, prix, quantite, sousTotal);
    }

    @Override
    public String toString() {
        return "LignePanierResponseDTO{" +
                "ligneId=" + ligneId +
                ", materielNom='" + materielNom + '\'' +
                ", quantite=" + quantite +
                ", sousTotal=" + sousTotal +
                '}';
    }

    public static LignePanierResponseDTOBuilder builder() {
        return new LignePanierResponseDTOBuilder();
    }

    public static class LignePanierResponseDTOBuilder {
        private Long ligneId;
        private Long materielId;
        private String materielNom;
        private double prix;
        private int quantite;
        private double sousTotal;

        public LignePanierResponseDTOBuilder ligneId(Long ligneId) { this.ligneId = ligneId; return this; }
        public LignePanierResponseDTOBuilder materielId(Long materielId) { this.materielId = materielId; return this; }
        public LignePanierResponseDTOBuilder materielNom(String materielNom) { this.materielNom = materielNom; return this; }
        public LignePanierResponseDTOBuilder prix(double prix) { this.prix = prix; return this; }
        public LignePanierResponseDTOBuilder quantite(int quantite) { this.quantite = quantite; return this; }
        public LignePanierResponseDTOBuilder sousTotal(double sousTotal) { this.sousTotal = sousTotal; return this; }

        public LignePanierResponseDTO build() {
            return new LignePanierResponseDTO(ligneId, materielId, materielNom, prix, quantite, sousTotal);
        }
    }
}
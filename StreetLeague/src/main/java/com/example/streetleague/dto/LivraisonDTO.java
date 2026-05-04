package com.example.streetleague.dto;

import com.example.streetleague.domain.LivraisonStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class LivraisonDTO {

    @NotNull(message = "Commande est obligatoire")
    private Long commandeId;

    @NotNull(message = "Transporteur est obligatoire")
    private Long transporteurId;

    private Long livreurId; // facultatif

    @NotBlank(message = "Adresse est obligatoire")
    private String adresse;

    @Min(value = 0, message = "Frais de livraison doit être >= 0")
    private double fraisLivraison;

    @NotNull(message = "Statut est obligatoire")
    private LivraisonStatus statut;

    public LivraisonDTO() {
    }

    public LivraisonDTO(Long commandeId, Long transporteurId, Long livreurId, String adresse, double fraisLivraison, LivraisonStatus statut) {
        this.commandeId = commandeId;
        this.transporteurId = transporteurId;
        this.livreurId = livreurId;
        this.adresse = adresse;
        this.fraisLivraison = fraisLivraison;
        this.statut = statut;
    }

    public Long getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(Long commandeId) {
        this.commandeId = commandeId;
    }

    public Long getTransporteurId() {
        return transporteurId;
    }

    public void setTransporteurId(Long transporteurId) {
        this.transporteurId = transporteurId;
    }

    public Long getLivreurId() {
        return livreurId;
    }

    public void setLivreurId(Long livreurId) {
        this.livreurId = livreurId;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public double getFraisLivraison() {
        return fraisLivraison;
    }

    public void setFraisLivraison(double fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public LivraisonStatus getStatut() {
        return statut;
    }

    public void setStatut(LivraisonStatus statut) {
        this.statut = statut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LivraisonDTO that = (LivraisonDTO) o;
        return Double.compare(that.fraisLivraison, fraisLivraison) == 0 && Objects.equals(commandeId, that.commandeId) && Objects.equals(transporteurId, that.transporteurId) && Objects.equals(livreurId, that.livreurId) && Objects.equals(adresse, that.adresse) && statut == that.statut;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandeId, transporteurId, livreurId, adresse, fraisLivraison, statut);
    }

    @Override
    public String toString() {
        return "LivraisonDTO{" +
                "commandeId=" + commandeId +
                ", transporteurId=" + transporteurId +
                ", adresse='" + adresse + '\'' +
                ", statut=" + statut +
                '}';
    }

    public static LivraisonDTOBuilder builder() {
        return new LivraisonDTOBuilder();
    }

    public static class LivraisonDTOBuilder {
        private Long commandeId;
        private Long transporteurId;
        private Long livreurId;
        private String adresse;
        private double fraisLivraison;
        private LivraisonStatus statut;

        public LivraisonDTOBuilder commandeId(Long commandeId) { this.commandeId = commandeId; return this; }
        public LivraisonDTOBuilder transporteurId(Long transporteurId) { this.transporteurId = transporteurId; return this; }
        public LivraisonDTOBuilder livreurId(Long livreurId) { this.livreurId = livreurId; return this; }
        public LivraisonDTOBuilder adresse(String adresse) { this.adresse = adresse; return this; }
        public LivraisonDTOBuilder fraisLivraison(double fraisLivraison) { this.fraisLivraison = fraisLivraison; return this; }
        public LivraisonDTOBuilder statut(LivraisonStatus statut) { this.statut = statut; return this; }

        public LivraisonDTO build() {
            return new LivraisonDTO(commandeId, transporteurId, livreurId, adresse, fraisLivraison, statut);
        }
    }
}
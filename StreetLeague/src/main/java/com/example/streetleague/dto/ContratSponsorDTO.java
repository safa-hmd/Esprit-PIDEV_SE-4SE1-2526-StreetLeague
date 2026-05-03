package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContratSponsorDTO {
    private Long id;
    private Long equipeId;
    private String titre;
    private String description;
    @JsonProperty("montantTotal")
    private Double montantTotal;
    @JsonProperty("dateDebut")
    private LocalDate dateDebut;
    @JsonProperty("dateFin")
    private LocalDate dateFin;
    private String statut;
    @JsonProperty("sponsorId")
    private Long sponsorId;
    @JsonProperty("sponsorNom")
    private String sponsorNom;
    @JsonProperty("dateCreation")
    private LocalDate dateCreation;
    private String conditions;
    private String typeContrat;

    public Long getSponsorId() { return sponsorId; }
    public void setSponsorId(Long sponsorId) { this.sponsorId = sponsorId; }
    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
}

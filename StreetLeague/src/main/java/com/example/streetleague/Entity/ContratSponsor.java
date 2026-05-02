package com.example.streetleague.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "contrat_sponsor")
public class ContratSponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sponsor_id", nullable = false)
    private Sponsor sponsor;

    @NotNull(message = "L'équipe est obligatoire")
    @Column(name = "equipe_id", nullable = false)
    private Long equipeId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateFin;

    @NotBlank(message = "Le statut est obligatoire")
    @Column(nullable = false, length = 50)
    private String statut;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conditions;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    public Long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}

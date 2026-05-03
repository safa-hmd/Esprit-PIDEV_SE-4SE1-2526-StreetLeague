package com.example.streetleague.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Entity
@Table(name = "sponsoring_evenement")
public class SponsoringEvenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sponsor_id", nullable = false)
    private Sponsor sponsor;

    @ManyToOne
    @JoinColumn(name = "evenement_id", nullable = false)
    private EvenementCommunaute evenement;

    @DecimalMin(value = "0.0", inclusive = false, message = "La contribution doit être positive")
    private BigDecimal contribution;

    private String typeContribution;

    @Column(name = "statut")
    private String statut;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Sponsor getSponsor() { return sponsor; }
    public void setSponsor(Sponsor sponsor) { this.sponsor = sponsor; }
    public EvenementCommunaute getEvenement() { return evenement; }
    public void setEvenement(EvenementCommunaute evenement) { this.evenement = evenement; }
    public BigDecimal getContribution() { return contribution; }
    public void setContribution(BigDecimal contribution) { this.contribution = contribution; }
    public String getTypeContribution() { return typeContribution; }
    public void setTypeContribution(String typeContribution) { this.typeContribution = typeContribution; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}

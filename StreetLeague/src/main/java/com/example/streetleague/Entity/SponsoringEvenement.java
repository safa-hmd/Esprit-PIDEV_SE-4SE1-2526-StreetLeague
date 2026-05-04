package com.example.streetleague.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.Objects;

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

    public SponsoringEvenement() {
    }

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

    public EvenementCommunaute getEvenement() {
        return evenement;
    }

    public void setEvenement(EvenementCommunaute evenement) {
        this.evenement = evenement;
    }

    public BigDecimal getContribution() {
        return contribution;
    }

    public void setContribution(BigDecimal contribution) {
        this.contribution = contribution;
    }

    public String getTypeContribution() {
        return typeContribution;
    }

    public void setTypeContribution(String typeContribution) {
        this.typeContribution = typeContribution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SponsoringEvenement that = (SponsoringEvenement) o;
        return Objects.equals(id, that.id) && Objects.equals(sponsor, that.sponsor) && Objects.equals(evenement, that.evenement) && Objects.equals(contribution, that.contribution) && Objects.equals(typeContribution, that.typeContribution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sponsor, evenement, contribution, typeContribution);
    }

    @Override
    public String toString() {
        return "SponsoringEvenement{" +
                "id=" + id +
                ", sponsor=" + sponsor +
                ", evenement=" + evenement +
                ", contribution=" + contribution +
                ", typeContribution='" + typeContribution + '\'' +
                '}';
    }
}


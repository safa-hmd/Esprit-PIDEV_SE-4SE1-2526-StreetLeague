package com.example.streetleague.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
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
}

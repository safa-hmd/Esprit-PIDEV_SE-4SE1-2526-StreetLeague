package com.example.streetleague.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "evenement_communaute")
public class EvenementCommunaute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String description;

    @FutureOrPresent(message = "La date doit être future ou présente")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "communaute_id", nullable = false)
    private Communaute communaute;

    @NotNull(message = "L'organisateur est obligatoire")
    private Long organisateurId;
}

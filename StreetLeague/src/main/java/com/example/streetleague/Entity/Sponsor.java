package com.example.streetleague.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sponsor")
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String type;

    @Email(message = "Email invalide")
    private String contactEmail;

    @Pattern(regexp = "\\d{8}", message = "Le téléphone doit contenir 8 chiffres")
    private String telephone;

    private String adresse;
}

package com.example.streetleague.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "communaute")
public class Communaute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 50, message = "Le nom doit contenir entre 3 et 50 caractères")
    private String nom;

    private String description;

    private String type;

    @PastOrPresent(message = "La date de création doit être passée ou présente")
    private Date dateCreation;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createurId;

    @OneToMany(mappedBy = "communaute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvenementCommunaute> evenements;

}

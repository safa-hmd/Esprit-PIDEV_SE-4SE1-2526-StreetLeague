package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "materiels")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String nom;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    double prix;

    @Column(nullable = false)
    int quantiteStock;

    String imageUrl;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
            @JsonIgnore
    Category categorie;
}
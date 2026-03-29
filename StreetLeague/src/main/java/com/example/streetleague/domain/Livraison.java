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
@Table(name = "livraisons")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "commande_id", nullable = false)
    Commande commande;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "transporteur_id", nullable = false)
    Transporteur transporteur;

    @ManyToOne
    @JoinColumn(name = "livreur_id")
    User livreur;

    String adresse;

    double fraisLivraison;

    @Enumerated(EnumType.STRING)
    LivraisonStatus statut;
}
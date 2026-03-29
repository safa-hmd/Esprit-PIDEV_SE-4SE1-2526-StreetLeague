package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;


@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ligne_commande")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
            @JsonIgnore
    Commande commande;

    @ManyToOne
    @JoinColumn(name = "materiel_id", nullable = false)
    Materiel materiel;

    int quantite;

    double prixUnitaire;
}
package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    // ← GARDER @JsonIgnore pour éviter la sérialisation circulaire
    //   mais exposer l'ID via @JsonProperty
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

    // ← AJOUTER ces deux méthodes pour exposer les IDs dans le JSON
    @JsonProperty("commandeId")
    public Long getCommandeId() {
        return commande != null ? commande.getId() : null;
    }

    @JsonProperty("transporteurId")
    public Long getTransporteurId() {
        return transporteur != null ? transporteur.getId() : null;
    }
}
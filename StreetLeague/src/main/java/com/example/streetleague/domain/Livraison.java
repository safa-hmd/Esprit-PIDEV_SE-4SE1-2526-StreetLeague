package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "livreur_id")
    User livreur;

    String adresse;

    @Builder.Default
    Double latitudeClient = 0.0;

    @Builder.Default
    Double longitudeClient = 0.0;

    @Builder.Default
    LocalDateTime dateCreation = LocalDateTime.now();

    LocalDateTime dateAffectation;
    LocalDateTime dateLivraison;

    @Builder.Default
    double fraisLivraison = 0.0;

    @Builder.Default
    Double distance = 0.0;

    // ✅ VARCHAR évite le conflit type ENUM MySQL natif
    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", columnDefinition = "VARCHAR(20) DEFAULT 'NORMAL'")
    @Builder.Default
    Priorite priorite = Priorite.NORMAL;

    @Builder.Default
    Double scoreAffectation = 0.0;

    @Enumerated(EnumType.STRING)
    LivraisonStatus statut;

    @Builder.Default
    int nbTentatives = 0;

    String motifEchec;

    @JsonProperty("commandeId")
    public Long getCommandeId() {
        return commande != null ? commande.getId() : null;
    }

    @JsonProperty("livreurId")
    public Long getLivreurId() {
        return livreur != null ? livreur.getId() : null;
    }
}
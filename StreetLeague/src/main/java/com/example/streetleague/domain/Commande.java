package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "commandes")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User user;

    double montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CommandeStatus statut;

    // ── PARTIE 2 : GPS client pour dispatching ───────────
    @Builder.Default
    Double latitudeClient = 0.0;

    @Builder.Default
    Double longitudeClient = 0.0;

    String adresseLivraison;

    private Long promoCodeId;
    private double discountAmount = 0.0;
    @Column(name = "date_creation")
    LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonIgnore
    List<LigneCommande> lignes;
}
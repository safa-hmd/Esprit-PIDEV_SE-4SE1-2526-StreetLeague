package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "paniers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User user;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    List<LignePanier> lignes = new ArrayList<>();

    // 🔹 Champs requis pour la détection d'abandon (> 48h sans activité)
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    // 🔹 Méthodes utilitaires pour gérer la liste des lignes proprement
    public void addLigne(LignePanier ligne) {
        lignes.add(ligne);
        ligne.setPanier(this);
    }

    public void removeLigne(LignePanier ligne) {
        lignes.remove(ligne);
        ligne.setPanier(null);
    }

    // 🔹 Calcul du total du panier (optionnel mais utile)
    public double calculerTotal() {
        return lignes.stream()
                .mapToDouble(l -> l.getQuantite() * l.getMateriel().getPrix())
                .sum();
    }
}
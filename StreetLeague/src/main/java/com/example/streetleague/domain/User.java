package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp; // ⚠️ Import ajouté
import java.time.LocalDateTime;                      // ⚠️ Import ajouté

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String fullName;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    @JsonIgnore
    String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    Role role;

    @Builder.Default
    boolean enabled = true;

    // ── PARTIE 1 : Champs tracking GPS livreur ──────────
    @Builder.Default
    Double latitude = 0.0;

    @Builder.Default
    Double longitude = 0.0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    LivreurStatus statusLivreur = LivreurStatus.OFFLINE;

    @Builder.Default
    int livraisonsEnCours = 0;

    // ── PARTIE 2 : Nouveau champ pour Promo WELCOME10 ──────────
    /**
     * Date d'inscription de l'utilisateur.
     * Remplie automatiquement par Hibernate à la création (INSERT).
     * Utilisé pour générer les codes promo "bienvenue" 24h après inscription.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    // ── Méthodes utilitaires (optionnelles mais recommandées) ──────────

    /**
     * Vérifie si l'utilisateur est un joueur (PLAYER)
     */
    public boolean isPlayer() {
        return Role.PLAYER.equals(this.role);
    }

    /**
     * Vérifie si l'utilisateur est un livreur actif
     */
    public boolean isActiveLivreur() {
        return Role.DELIVERY.equals(this.role) && LivreurStatus.DISPONIBLE.equals(this.statusLivreur);
    }
}
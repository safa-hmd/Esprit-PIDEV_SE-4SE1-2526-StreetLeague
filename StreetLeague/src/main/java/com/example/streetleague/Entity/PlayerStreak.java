package com.example.streetleague.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.AccessLevel;
import org.springframework.data.domain.Persistable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PlayerStreak — une ligne par joueur (playerId = PK).
 *
 * IMPORTANT : l'ID est assigné manuellement (= l'ID du joueur),
 * donc on doit implémenter Persistable pour distinguer INSERT vs UPDATE.
 *
 * Règle : isNew = true  → Spring fait persist() = INSERT
 *         isNew = false → Spring fait merge()   = UPDATE
 *
 * @PostLoad marque l'entité "pas nouvelle" dès qu'elle est chargée depuis la DB.
 * @PostPersist marque "pas nouvelle" après un INSERT pour éviter un double INSERT.
 *
 * NE PAS utiliser le @Builder pour créer une nouvelle entité :
 * utiliser new PlayerStreak() + setters, car le Builder met isNew = true
 * par défaut (ce qui est correct pour de nouvelles entités),
 * mais peut causer une confusion si l'entité existe déjà.
 */
@Entity
@Table(name = "player_streaks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(toBuilder = true)
public class PlayerStreak implements Persistable<Long> {

    @Id
    @Column(name = "player_id", nullable = false, unique = true)
    private Long playerId;

    /**
     * Transient : non persisté. Vrai par défaut = nouvelle entité à insérer.
     * Mis à false après chargement (@PostLoad) ou insertion (@PostPersist).
     *
     * NOTE : pas de @Builder.Default ici — on utilise l'initialisation directe
     * pour que isNew=true fonctionne autant avec new PlayerStreak() qu'avec
     * PlayerStreak.builder().build().
     */
    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public Long getId() { return playerId; }

    @Override
    public boolean isNew() { return isNew; }

    @PostPersist
    @PostLoad
    void markNotNew() { this.isNew = false; }

    @Builder.Default
    private Integer currentStreak = 0;

    @Builder.Default
    private Integer bestStreak = 0;

    @Builder.Default
    private Integer totalPoints = 0;

    @Builder.Default
    private Double momentumScore = 0.0;

    @Builder.Default
    private Double fatigueRisk = 0.0;

    private String currentBadge;
    private LocalDate lastAttendanceDate;
    private LocalDateTime lastUpdated;
    private LocalDateTime synergyBonusAwardedAt;
}
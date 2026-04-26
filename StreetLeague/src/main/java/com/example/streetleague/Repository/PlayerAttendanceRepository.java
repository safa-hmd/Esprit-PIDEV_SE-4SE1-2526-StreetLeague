package com.example.streetleague.Repository;

import com.example.streetleague.Entity.PlayerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerAttendanceRepository extends JpaRepository<PlayerAttendance, Long> {

    // Présences d'un joueur sur une période (utilisée dans updateStreak)
    List<PlayerAttendance> findByPlayerIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long playerId, LocalDate from, LocalDate to);

    // Vérifier si déjà check-in aujourd'hui
    Optional<PlayerAttendance> findByPlayerIdAndAttendanceDate(Long playerId, LocalDate date);

    // Statistiques globales — nombre total de jours présents
    @Query("SELECT COUNT(a) FROM PlayerAttendance a WHERE a.playerId = :playerId AND a.isPresent = true")
    Integer countTotalPresentDays(@Param("playerId") Long playerId);

    /*
     * CORRECTION : calculateCurrentStreak() SUPPRIMÉE.
     *
     * L'ancienne requête SQL :
     *   SELECT COUNT(*) FROM (SELECT ... ORDER BY date DESC LIMIT 30)
     * comptait simplement les 30 dernières présences sans vérifier
     * la CONSÉCUTIVITÉ. Un joueur absent 2 semaines puis présent 5 jours
     * aurait eu un "streak" artificiellement gonflé.
     *
     * Le calcul correct du streak consécutif est dans PerformanceService.updateStreak()
     * via la boucle Java qui vérifie chaque jour depuis aujourd'hui et s'arrête
     * au premier jour d'absence. C'est la seule implémentation à utiliser.
     */
}
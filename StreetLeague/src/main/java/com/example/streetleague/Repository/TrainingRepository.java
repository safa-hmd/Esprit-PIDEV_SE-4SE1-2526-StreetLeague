package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Training;
import com.example.streetleague.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    // JPQL avec JOIN entre Team, Training et User (coach)
// Retourne les trainings PLANNED du futur, triés par date
    @Query("""
    SELECT t FROM Training t JOIN FETCH t.team team JOIN FETCH t.coach coach
    WHERE t.status = com.example.streetleague.Entity.TrainingStatus.PLANNED
      AND t.trainingDate > :now ORDER BY t.trainingDate ASC
""")
    List<Training> findUpcomingTrainingsWithTeamAndCoach(@Param("now") LocalDateTime now);

    // JPQL avec JOIN FETCH entre Training, Team et Participants (Post Training avancé)
    @Query("""
    SELECT t FROM Training t 
    JOIN FETCH t.team team 
    LEFT JOIN FETCH t.participants 
    WHERE team.idTeam = :teamId AND t.status = com.example.streetleague.Entity.TrainingStatus.COMPLETED
    ORDER BY t.trainingDate DESC
    """)
    List<Training> findCompletedTrainingsWithParticipantsByTeam(@Param("teamId") Long teamId);



    // AJOUTER dans TrainingRepository.java

    // Pour le COACH — ses trainings dans une période
    List<Training> findByCoachAndTrainingDateBetween(
            User coach,
            LocalDateTime from,
            LocalDateTime to
    );

    // Pour le PLAYER — trainings où il est participant
    @Query("""
    SELECT t FROM Training t
    JOIN t.participants p
    WHERE p = :user
    AND t.trainingDate BETWEEN :from AND :to
""")
    List<Training> findByParticipantAndDateBetween(
            @Param("user") User user,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}

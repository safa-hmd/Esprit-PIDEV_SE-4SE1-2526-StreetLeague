package com.example.streetleague.Repository;


import com.example.streetleague.Entity.RegistrationStatus;
import com.example.streetleague.Entity.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {

    List<TournamentRegistration> findByTournamentId(Long tournamentId);

    List<TournamentRegistration> findByPlayerIdUser(Long playerId);

    List<TournamentRegistration> findByTeamIdTeam(Long teamId);

   // Optional<TournamentRegistration> findByTournamentIdAndPlayerId(Long tournamentId, Long playerId);

    //Optional<TournamentRegistration> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);

    long countByTournamentIdAndStatus(Long tournamentId, RegistrationStatus status);

    boolean existsByTournamentIdAndPlayerIdUser(Long tournamentId, Long playerId);

    boolean existsByTournamentIdAndTeamIdTeam(Long tournamentId, Long teamId);
}

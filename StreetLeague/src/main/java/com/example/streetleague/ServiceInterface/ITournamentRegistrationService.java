package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.TournamentRegistrationDto;

import java.util.List;

public interface ITournamentRegistrationService {
    TournamentRegistrationDto registerPlayer(Long tournamentId, Long playerId);
    TournamentRegistrationDto registerTeam(Long tournamentId, Long teamId);
    TournamentRegistrationDto getRegistrationById(Long id);
    List<TournamentRegistrationDto> getRegistrationsByTournament(Long tournamentId);
    List<TournamentRegistrationDto> getRegistrationsByPlayer(Long playerId);
    List<TournamentRegistrationDto> getRegistrationsByTeam(Long teamId);
    TournamentRegistrationDto cancelRegistration(Long id);
    void deleteRegistration(Long id);

    List<TournamentRegistrationDto> getTeamRegistrationsByPlayer(Long playerId);
    TournamentRegistrationDto acceptRegistration(Long id);
    TournamentRegistrationDto rejectRegistration(Long id);
}

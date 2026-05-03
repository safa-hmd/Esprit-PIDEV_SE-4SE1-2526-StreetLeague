package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.TournamentDto;

import java.util.List;

public interface ITournamentService {
    TournamentDto createTournament(TournamentDto dto);
    TournamentDto updateTournament(Long id, TournamentDto dto);
    void deleteTournament(Long id);
    List<TournamentDto> getAllTournaments();

    TournamentDto getTournamentById(Long id);
    List<TournamentDto> getUpcomingTournaments();
    TournamentDto cancelTournament(Long id);

}

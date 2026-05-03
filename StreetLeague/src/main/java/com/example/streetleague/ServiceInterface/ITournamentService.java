package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.TournamentDto;
import java.util.List;

public interface ITournamentService {
    List<TournamentDto> getAllTournaments();
    TournamentDto getTournamentById(Long id);
    TournamentDto createTournament(TournamentDto tournamentDto);
    TournamentDto updateTournament(Long id, TournamentDto tournamentDto);
    void deleteTournament(Long id);
    TournamentDto cancelTournament(Long id);
    List<TournamentDto> getUpcomingTournaments();

    // Optional methods with default stubs
    default List<TournamentDto> getTournamentsByStatus(String status) { return List.of(); }
    default List<TournamentDto> getTournamentsBySportType(String sportType) { return List.of(); }
    default List<TournamentDto> searchTournaments(String keyword) { return List.of(); }
}

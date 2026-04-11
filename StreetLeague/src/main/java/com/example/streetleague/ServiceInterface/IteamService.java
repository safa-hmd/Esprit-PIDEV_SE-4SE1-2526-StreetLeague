package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.TeamRequest;
import com.example.streetleague.dto.TeamResponse;

import java.util.List;

public interface IteamService {
    TeamResponse addTeam(TeamRequest t, Long captainId);
    TeamResponse updateTeam(Long teamId, TeamRequest t, Long captainId);
    void deleteTeam(Long idTeam, Long captainId);
    List<TeamResponse> ShowTeams();
    TeamResponse ShowTeam(Long idTeam);
    List<TeamResponse> getTeamsByCaptain(Long captainId);
    TeamResponse updateTeamStats(Long teamId, int victories, int defeats, int matches, Long captainId);

    // Leaderboard
    List<TeamResponse> getLeaderboard(String sport);

    TeamResponse joinTeam(Long teamId, Long playerId);
    TeamResponse leaveTeam(Long teamId, Long playerId);
}
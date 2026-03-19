package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.IteamService;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TeamRequest;
import com.example.streetleague.dto.TeamResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TeamServiceImpl implements IteamService {

    TeamRepository teamRepository;
    UserRepository userRepository;
    MatchRepository matchRepository;

    @Override
    public TeamResponse addTeam(TeamRequest dto, Long captainId) {
        User captain = userRepository.findById(captainId)
                .orElseThrow(() -> new RuntimeException("User not found: " + captainId));

        if (captain.getRole() != Role.COACH && captain.getRole() != Role.PLAYER)
            throw new RuntimeException("Only a COACH or PLAYER can create a team");
        if (dto.name() == null || dto.name().isBlank())
            throw new RuntimeException("Team name is required");
        if (dto.name().length() < 2 || dto.name().length() > 50)
            throw new RuntimeException("Team name must be between 2 and 50 characters");
        boolean nameExists = teamRepository.findAll().stream()
                .anyMatch(team -> team.getName().equalsIgnoreCase(dto.name()));
        if (nameExists)
            throw new RuntimeException("A team with this name already exists");
        if (dto.sport() == null || dto.sport().isBlank())
            throw new RuntimeException("Sport is required");
        if (dto.sport().length() < 2 || dto.sport().length() > 50)
            throw new RuntimeException("Sport must be between 2 and 50 characters");
        if (dto.level() == null)
            throw new RuntimeException("Level is required (BEGINNER, INTERMEDIATE, ADVANCED)");
        if (dto.description() != null && dto.description().length() > 255)
            throw new RuntimeException("Description cannot exceed 255 characters");

        // Conversion DTO → Entity + injection des valeurs auto
        Team t = new Team();
        t.setName(dto.name());
        t.setSport(dto.sport());
        t.setDescription(dto.description());
        t.setLevel(dto.level());
        t.setCaptain(captain);
        t.setCreationDate(LocalDate.now());

        return TeamResponse.fromEntity(teamRepository.save(t));
    }

    @Override
    public TeamResponse updateTeam(Long teamId, TeamRequest dto, Long captainId) {
        Team existing = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        if (!existing.getCaptain().getIdUser().equals(captainId))
            throw new RuntimeException("Only the team captain can update this team");

        if (dto.name() != null) {
            if (dto.name().isBlank())
                throw new RuntimeException("Team name cannot be empty");
            if (dto.name().length() < 2 || dto.name().length() > 50)
                throw new RuntimeException("Team name must be between 2 and 50 characters");
            boolean nameExists = teamRepository.findAll().stream()
                    .anyMatch(team -> team.getName().equalsIgnoreCase(dto.name())
                            && !team.getIdTeam().equals(teamId));
            if (nameExists)
                throw new RuntimeException("A team with this name already exists");
            existing.setName(dto.name());
        }
        if (dto.sport() != null) {
            if (dto.sport().isBlank())
                throw new RuntimeException("Sport cannot be empty");
            if (dto.sport().length() < 2 || dto.sport().length() > 50)
                throw new RuntimeException("Sport must be between 2 and 50 characters");
            existing.setSport(dto.sport());
        }
        if (dto.description() != null) {
            if (dto.description().length() > 255)
                throw new RuntimeException("Description cannot exceed 255 characters");
            existing.setDescription(dto.description());
        }
        if (dto.level() != null)
            existing.setLevel(dto.level());

        return TeamResponse.fromEntity(teamRepository.save(existing));
    }

    @Override
    public void deleteTeam(Long idTeam, Long captainId) {
        Team team = teamRepository.findById(idTeam)
                .orElseThrow(() -> new RuntimeException("Team not found: " + idTeam));

        if (!team.getCaptain().getIdUser().equals(captainId))
            throw new RuntimeException("Only the team captain can delete this team");

        // ✅ Supprimer les matchs associés avant de supprimer la team
        matchRepository.deleteByTeamAIdOrTeamBId(idTeam, idTeam);

        teamRepository.deleteById(idTeam);
    }

    @Override
    public List<TeamResponse> ShowTeams() {
        return teamRepository.findAll().stream()
                .map(TeamResponse::fromEntity).toList();
    }

    @Override
    public TeamResponse ShowTeam(Long idTeam) {
        return TeamResponse.fromEntity(
                teamRepository.findById(idTeam)
                        .orElseThrow(() -> new RuntimeException("Team not found: " + idTeam)));
    }

    @Override
    public TeamResponse joinTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + playerId));

        if (player.getRole() != Role.PLAYER)
            throw new RuntimeException("Only a PLAYER can join a team");
        if (team.getCaptain().getIdUser().equals(playerId))
            throw new RuntimeException("The captain cannot join their own team as a player");
        if (team.getPlayers().contains(player))
            throw new RuntimeException("Player is already in this team");

        team.getPlayers().add(player);
        return TeamResponse.fromEntity(teamRepository.save(team));
    }

    @Override
    public TeamResponse leaveTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + playerId));

        if (!team.getPlayers().contains(player))
            throw new RuntimeException("Player is not part of this team");

        team.getPlayers().remove(player);
        return TeamResponse.fromEntity(teamRepository.save(team));
    }
}
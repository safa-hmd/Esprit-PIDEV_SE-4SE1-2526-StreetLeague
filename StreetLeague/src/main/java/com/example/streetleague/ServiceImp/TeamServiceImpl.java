package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.IteamService;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.LeaderboardDto;
import com.example.streetleague.dto.TeamRequest;
import com.example.streetleague.dto.TeamResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TeamServiceImpl implements IteamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    public TeamServiceImpl(TeamRepository teamRepository, 
                           UserRepository userRepository, 
                           MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional
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
    @Transactional
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
    @Transactional

    public TeamResponse updateTeamStats(Long teamId, int victories, int defeats, int matches, Long userId) {
        Team existing = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        // ← Supprime ou assouplit cette vérification
        // if (!existing.getCaptain().getIdUser().equals(captainId)) ...

        existing.setVictories(victories);
        existing.setDefeats(defeats);
        existing.setMatches(matches);

        return TeamResponse.fromEntity(teamRepository.save(existing));
    }

    @Override
    public void deleteTeam(Long idTeam, Long userId) {
        Team team = teamRepository.findById(idTeam)
                .orElseThrow(() -> new RuntimeException("Team not found: " + idTeam));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // ✅ ADMIN peut supprimer n'importe quelle team, sinon seulement le capitaine
        boolean isAdmin = user.getRole() == Role.ADMIN;
        if (!isAdmin && !team.getCaptain().getIdUser().equals(userId))
            throw new RuntimeException("Only the team captain or an admin can delete this team");

        // ✅ 1. Supprimer les matchs associés (teamA ou teamB)
        matchRepository.deleteByTeamAIdOrTeamBId(idTeam);

        // ✅ 2. Retirer tous les joueurs de la team (évite la contrainte FK)
        team.getPlayers().clear();
        teamRepository.save(team);

        // ✅ 3. Supprimer la team
        teamRepository.deleteById(idTeam);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TeamResponse> ShowTeams() {
        return teamRepository.findAll().stream()
                .map(TeamResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public TeamResponse ShowTeam(Long idTeam) {
        return TeamResponse.fromEntity(
                teamRepository.findById(idTeam)
                        .orElseThrow(() -> new RuntimeException("Team not found: " + idTeam)));
    }

    @Override
    @Transactional
    public List<TeamResponse> getTeamsByCaptain(Long captainId) {
        return teamRepository.findAll().stream()
                .filter(t -> t.getCaptain().getIdUser().equals(captainId))
                .map(TeamResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
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

        int maxPlayers = 11;
        String sportStr = team.getSport() != null ? team.getSport().toLowerCase() : "";
        if (sportStr.contains("football") || sportStr.contains("soccer")) {
            maxPlayers = 11;
        } else if (sportStr.contains("basketball")) {
            maxPlayers = 5;
        } else if (sportStr.contains("volleyball")) {
            maxPlayers = 6;
        } else if (sportStr.contains("handball")) {
            maxPlayers = 7;
        } else if (sportStr.contains("tennis")) {
            maxPlayers = 2; // doubles
        } else if (sportStr.contains("rugby")) {
            maxPlayers = 15;
        } else if (sportStr.contains("baseball")) {
            maxPlayers = 9;
        } else if (sportStr.contains("cricket")) {
            maxPlayers = 11;
        } else if (sportStr.contains("padel")) {
            maxPlayers = 2;
        }
        
        // Captain acts as a player but is not in getPlayers()
        if (team.getPlayers().size() + 1 >= maxPlayers) {
            throw new RuntimeException("The team is full for this sport (" + team.getSport() + " allows a maximum of " + maxPlayers + " players)");
        }

        team.getPlayers().add(player);
        return TeamResponse.fromEntity(teamRepository.save(team));
    }

    @Override
    @Transactional
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


    @Override
    public List<LeaderboardDto> getLeaderboard(String sport) {
        return teamRepository.findLeaderboardBySport(sport)
                .stream()
                .limit(5)
                .toList();
    }
}
package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Match;
import com.example.streetleague.Entity.MatchStatus;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.MatchRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.ImatchService;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.MatchRequest;
import com.example.streetleague.dto.MatchResponse;
import com.example.streetleague.dto.MatchUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MatchServiceImpl implements ImatchService {

    MatchRepository matchRepository;
    TeamRepository  teamRepository;
    UserRepository  userRepository;

    @Override
    public MatchResponse addMatch(MatchRequest dto, Long teamAId, Long teamBId, Long captainId) {
        Team teamA = teamRepository.findById(teamAId)
                .orElseThrow(() -> new RuntimeException("TeamA not found: " + teamAId));
        Team teamB = teamRepository.findById(teamBId)
                .orElseThrow(() -> new RuntimeException("TeamB not found: " + teamBId));
        User captain = userRepository.findById(captainId)
                .orElseThrow(() -> new RuntimeException("User not found: " + captainId));

        if (captain.getRole() != Role.PLAYER && captain.getRole() != Role.COACH)
            throw new RuntimeException("Only a PLAYER or COACH can create a match");
        if (!teamA.getCaptain().getIdUser().equals(captainId))
            throw new RuntimeException("Only the captain of TeamA can send a match request");
        if (teamAId.equals(teamBId))
            throw new RuntimeException("A team cannot play against itself");
        if (dto.location() == null || dto.location().isBlank())
            throw new RuntimeException("Location is required");
        if (dto.location().length() < 3 || dto.location().length() > 100)
            throw new RuntimeException("Location must be between 3 and 100 characters");
//        if (dto.matchDate() == null)
//            throw new RuntimeException("Match date is required");
//        if (dto.matchDate().isBefore(LocalDateTime.now()))
//            throw new RuntimeException("Match date must be in the future");

        boolean alreadyExists = matchRepository.findAll().stream().anyMatch(e ->
                e.getTeamA().getIdTeam().equals(teamAId) &&
                        e.getTeamB().getIdTeam().equals(teamBId) &&
                        (e.getStatus() == MatchStatus.PENDING || e.getStatus() == MatchStatus.ACCEPTED));
        if (alreadyExists)
            throw new RuntimeException("A pending/accepted match already exists between these teams");

        // Conversion DTO → Entity + injection des relations
        Match m = new Match();
        m.setMatchDate(dto.matchDate());
        m.setLocation(dto.location());
        m.setTeamA(teamA);
        m.setTeamB(teamB);
        m.setCreatedBy(captain);
        m.setStatus(MatchStatus.PENDING);

        return MatchResponse.fromEntity(matchRepository.save(m));
    }

    @Override
    public MatchResponse updateMatch(MatchUpdateRequest dto, Long captainId) {
        Match existing = matchRepository.findById(dto.idMatch())
                .orElseThrow(() -> new RuntimeException("Match not found: " + dto.idMatch()));

        if (!existing.getCreatedBy().getIdUser().equals(captainId))
            throw new RuntimeException("Only the captain who created this match can edit it");
        if (existing.getStatus() == MatchStatus.FINISHED || existing.getStatus() == MatchStatus.CANCELLED)
            throw new RuntimeException("Cannot edit a finished or cancelled match");

        if (dto.location() != null) {
            if (dto.location().isBlank())
                throw new RuntimeException("Location cannot be empty");
            if (dto.location().length() < 3 || dto.location().length() > 100)
                throw new RuntimeException("Location must be between 3 and 100 characters");
            existing.setLocation(dto.location());
        }
        if (dto.matchDate() != null) {
            if (dto.matchDate().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Match date must be in the future");
            existing.setMatchDate(dto.matchDate());
        }
        if (dto.status() == MatchStatus.FINISHED) {
            if (dto.scoreTeamA() == null || dto.scoreTeamB() == null)
                throw new RuntimeException("Scores are required when setting match as FINISHED");
            if (dto.scoreTeamA() < 0 || dto.scoreTeamB() < 0)
                throw new RuntimeException("Scores cannot be negative");
            existing.setScoreTeamA(dto.scoreTeamA());
            existing.setScoreTeamB(dto.scoreTeamB());
        }
        if (dto.status() != null)
            existing.setStatus(dto.status());

        return MatchResponse.fromEntity(matchRepository.save(existing));
    }

    @Override
    public void deleteMatch(Long idMatch, Long userId) {
        Match match = matchRepository.findById(idMatch)
                .orElseThrow(() -> new RuntimeException("Match not found: " + idMatch));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // ✅ ADMIN peut supprimer n'importe quel match
        boolean isAdmin = user.getRole() == Role.ADMIN;

        boolean isCaptainA = match.getTeamA().getCaptain().getIdUser().equals(userId);
        boolean isCaptainB = match.getTeamB().getCaptain().getIdUser().equals(userId);

        if (!isAdmin && !isCaptainA && !isCaptainB)
            throw new RuntimeException("Only the captain of TeamA or TeamB can delete this match");

        if (match.getStatus() == MatchStatus.FINISHED)
            throw new RuntimeException("Cannot delete a finished match");

        matchRepository.deleteById(idMatch);
    }


    @Override
    public List<MatchResponse> ShowMatchs() {
        return matchRepository.findAll().stream()
                .map(MatchResponse::fromEntity).toList();
    }

    @Override
    public MatchResponse ShowMatch(Long idMatch) {
        return MatchResponse.fromEntity(
                matchRepository.findById(idMatch)
                        .orElseThrow(() -> new RuntimeException("Match not found: " + idMatch)));
    }
}
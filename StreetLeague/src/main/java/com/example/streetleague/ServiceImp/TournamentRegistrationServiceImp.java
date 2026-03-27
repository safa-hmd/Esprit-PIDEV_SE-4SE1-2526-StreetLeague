package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.TournamentRegistrationRepository;
import com.example.streetleague.Repository.TournamentRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.ITournamentRegistrationService;
import com.example.streetleague.domain.User;
import com.example.streetleague.Entity.*;
import com.example.streetleague.dto.TournamentRegistrationDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TournamentRegistrationServiceImp implements ITournamentRegistrationService {

    private final TournamentRegistrationRepository registrationRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    public TournamentRegistrationDto registerPlayer(Long tournamentId, Long playerId) {
        Tournament tournament = findTournamentById(tournamentId);

        if (tournament.getTournamentType() != TournamentType.INDIVIDUAL) {
            throw new IllegalStateException("This tournament is for teams only");
        }
        validateTournamentOpen(tournament);

        if (registrationRepository.existsByTournamentIdAndPlayerIdUser(tournamentId, playerId)) {
            throw new IllegalArgumentException("Player is already registered for this tournament");
        }

        long confirmed = registrationRepository.countByTournamentIdAndStatus(tournamentId, RegistrationStatus.CONFIRMED);
        if (confirmed >= tournament.getMaxParticipants()) {
            throw new IllegalStateException("Tournament is full");
        }

        User player = findUserById(playerId);

        TournamentRegistration registration = TournamentRegistration.builder()
                .tournament(tournament)
                .player(player)
                .status(RegistrationStatus.PENDING)
                .build();

        return mapToDto(registrationRepository.save(registration));
    }

    @Override
    public TournamentRegistrationDto registerTeam(Long tournamentId, Long teamId) {
        Tournament tournament = findTournamentById(tournamentId);

        if (tournament.getTournamentType() != TournamentType.TEAM) {
            throw new IllegalStateException("This tournament is for individual players only");
        }
        validateTournamentOpen(tournament);

        if (registrationRepository.existsByTournamentIdAndTeamIdTeam(tournamentId, teamId)) {
            throw new IllegalArgumentException("Team is already registered for this tournament");
        }

        long confirmed = registrationRepository.countByTournamentIdAndStatus(tournamentId, RegistrationStatus.CONFIRMED);
        if (confirmed >= tournament.getMaxParticipants()) {
            throw new IllegalStateException("Tournament is full");
        }

        Team team = findTeamById(teamId);

        TournamentRegistration registration = TournamentRegistration.builder()
                .tournament(tournament)
                .team(team)
                .status(RegistrationStatus.PENDING)
                .build();

        return mapToDto(registrationRepository.save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentRegistrationDto getRegistrationById(Long id) {
        return mapToDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentRegistrationDto> getRegistrationsByTournament(Long tournamentId) {
        return registrationRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentRegistrationDto> getRegistrationsByPlayer(Long playerId) {
        return registrationRepository.findByPlayerIdUser(playerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentRegistrationDto> getRegistrationsByTeam(Long teamId) {
        return registrationRepository.findByTeamIdTeam(teamId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TournamentRegistrationDto cancelRegistration(Long id) {
        TournamentRegistration registration = findById(id);
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new IllegalStateException("Registration is already cancelled");
        }
        registration.setStatus(RegistrationStatus.CANCELLED);
        return mapToDto(registrationRepository.save(registration));
    }

    @Override
    public void deleteRegistration(Long id) {
        TournamentRegistration registration = findById(id);
        registrationRepository.delete(registration);
    }

    // ---- Helpers ----

    private void validateTournamentOpen(Tournament tournament) {
        if (tournament.getStatus() != TournamentStatus.UPCOMING) {
            throw new IllegalStateException("Tournament is not open for registration");
        }
        if (tournament.getRegistrationDeadline() != null &&
                LocalDate.now().isAfter(tournament.getRegistrationDeadline())) {
            throw new IllegalStateException("Registration deadline has passed");
        }
    }

    private TournamentRegistration findById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));
    }

    private Tournament findTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Team findTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
    }

    private TournamentRegistrationDto mapToDto(TournamentRegistration r) {
        return TournamentRegistrationDto.builder()
                .id(r.getId())
                .tournamentId(r.getTournament().getId())
                .tournamentName(r.getTournament().getName())
                .playerId(r.getPlayer() != null ? r.getPlayer().getIdUser() : null)
                .playerUsername(r.getPlayer() != null ? r.getPlayer().getFullName() : null)
                .teamId(r.getTeam() != null ? r.getTeam().getIdTeam() : null)
                .teamName(r.getTeam() != null ? r.getTeam().getName() : null)
                .status(r.getStatus())
                .registeredAt(r.getRegisteredAt())
                .build();
    }

    //--------------------------------------ADMIN-------------------------------
    //-------------------------ACCEPT OR REJECT REGISTRATION --------------

    public TournamentRegistrationDto acceptRegistration(Long id) {
        TournamentRegistration reg = findById(id);
        if (reg.getStatus() != RegistrationStatus.PENDING)
            throw new IllegalStateException("Registration is not pending");

        // Vérifier que le tournoi n'est pas plein
        long confirmed = registrationRepository
                .countByTournamentIdAndStatus(reg.getTournament().getId(),
                        RegistrationStatus.CONFIRMED);
        if (confirmed >= reg.getTournament().getMaxParticipants())
            throw new IllegalStateException("Tournament is full");

        reg.setStatus(RegistrationStatus.CONFIRMED);
        return mapToDto(registrationRepository.save(reg));
    }

    public TournamentRegistrationDto rejectRegistration(Long id) {
        TournamentRegistration reg = findById(id);
        if (reg.getStatus() != RegistrationStatus.PENDING)
            throw new IllegalStateException("Registration is not pending");
        reg.setStatus(RegistrationStatus.CANCELLED);
        return mapToDto(registrationRepository.save(reg));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentRegistrationDto> getTeamRegistrationsByPlayer(Long playerId) {
        // Vérifie que le joueur existe
        findUserById(playerId);

        return registrationRepository
                .findTeamRegistrationsByPlayerId(playerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}


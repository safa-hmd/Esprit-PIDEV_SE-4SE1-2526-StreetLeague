package com.example.streetleague;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.TournamentRegistrationServiceImp;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TournamentRegistrationDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentRegistrationServiceImpTest {

    @Mock TournamentRegistrationRepository registrationRepository;
    @Mock TournamentRepository tournamentRepository;
    @Mock UserRepository userRepository;
    @Mock TeamRepository teamRepository;
    @InjectMocks TournamentRegistrationServiceImp service;

    private Tournament individualTournament;
    private Tournament teamTournament;
    private User player;
    private Team team;
    private TournamentRegistration pendingRegistration;

    @BeforeEach
    void setUp() {
        player = new User();
        player.setIdUser(1L);
        player.setFullName("Ali Ben");

        team = Team.builder()
                .idTeam(1L)
                .name("Eagles FC")
                .build();

        individualTournament = Tournament.builder()
                .id(1L)
                .name("Solo Cup")
                .tournamentType(TournamentType.INDIVIDUAL)
                .status(TournamentStatus.UPCOMING)
                .registrationDeadline(LocalDate.now().plusDays(5))
                .maxParticipants(8)
                .registrations(List.of())
                .build();

        teamTournament = Tournament.builder()
                .id(2L)
                .name("Team Cup")
                .tournamentType(TournamentType.TEAM)
                .status(TournamentStatus.UPCOMING)
                .registrationDeadline(LocalDate.now().plusDays(5))
                .maxParticipants(8)
                .registrations(List.of())
                .build();

        pendingRegistration = TournamentRegistration.builder()
                .id(1L)
                .tournament(individualTournament)
                .player(player)
                .status(RegistrationStatus.PENDING)
                .build();
    }

    // ── REGISTER PLAYER ───────────────────────────────────────────────────────

    @Test
    void registerPlayer_success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(individualTournament));
        when(registrationRepository.existsByTournamentIdAndPlayerIdUser(1L, 1L)).thenReturn(false);
        when(registrationRepository.countByTournamentIdAndStatus(1L, RegistrationStatus.CONFIRMED)).thenReturn(0L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(player));
        when(registrationRepository.save(any())).thenReturn(pendingRegistration);

        TournamentRegistrationDto result = service.registerPlayer(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.PENDING);
        verify(registrationRepository).save(any(TournamentRegistration.class));
    }

    @Test
    void registerPlayer_wrongTournamentType_throwsException() {
        when(tournamentRepository.findById(2L)).thenReturn(Optional.of(teamTournament));

        assertThatThrownBy(() -> service.registerPlayer(2L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("teams only");
    }

    @Test
    void registerPlayer_alreadyRegistered_throwsException() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(individualTournament));
        when(registrationRepository.existsByTournamentIdAndPlayerIdUser(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.registerPlayer(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void registerPlayer_tournamentFull_throwsException() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(individualTournament));
        when(registrationRepository.existsByTournamentIdAndPlayerIdUser(1L, 1L)).thenReturn(false);
        when(registrationRepository.countByTournamentIdAndStatus(1L, RegistrationStatus.CONFIRMED))
                .thenReturn(8L); // maxParticipants = 8

        assertThatThrownBy(() -> service.registerPlayer(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("full");
    }

    @Test
    void registerPlayer_tournamentNotOpen_throwsException() {
        individualTournament.setStatus(TournamentStatus.COMPLETED);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(individualTournament));
        // ← supprimé : existsByTournamentIdAndPlayerIdUser (jamais atteint)

        assertThatThrownBy(() -> service.registerPlayer(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not open for registration");
    }

    @Test
    void registerPlayer_deadlinePassed_throwsException() {
        individualTournament.setRegistrationDeadline(LocalDate.now().minusDays(1));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(individualTournament));
        // ← supprimé : existsByTournamentIdAndPlayerIdUser (jamais atteint)

        assertThatThrownBy(() -> service.registerPlayer(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("deadline has passed");
    }

    // ── REGISTER TEAM ─────────────────────────────────────────────────────────

    @Test
    void registerTeam_success() {
        TournamentRegistration teamReg = TournamentRegistration.builder()
                .id(2L).tournament(teamTournament).team(team)
                .status(RegistrationStatus.PENDING).build();

        when(tournamentRepository.findById(2L)).thenReturn(Optional.of(teamTournament));
        when(registrationRepository.existsByTournamentIdAndTeamIdTeam(2L, 1L)).thenReturn(false);
        when(registrationRepository.countByTournamentIdAndStatus(2L, RegistrationStatus.CONFIRMED)).thenReturn(0L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(registrationRepository.save(any())).thenReturn(teamReg);

        TournamentRegistrationDto result = service.registerTeam(2L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.PENDING);
    }

    @Test
    void registerTeam_wrongTournamentType_throwsException() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(individualTournament));

        assertThatThrownBy(() -> service.registerTeam(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("individual players only");
    }

    @Test
    void registerTeam_alreadyRegistered_throwsException() {
        when(tournamentRepository.findById(2L)).thenReturn(Optional.of(teamTournament));
        when(registrationRepository.existsByTournamentIdAndTeamIdTeam(2L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.registerTeam(2L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    // ── ACCEPT / REJECT ───────────────────────────────────────────────────────

    @Test
    void acceptRegistration_success() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));
        when(registrationRepository.countByTournamentIdAndStatus(1L, RegistrationStatus.CONFIRMED)).thenReturn(0L);
        when(registrationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TournamentRegistrationDto result = service.acceptRegistration(1L);

        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.CONFIRMED);
    }

    @Test
    void acceptRegistration_notPending_throwsException() {
        pendingRegistration.setStatus(RegistrationStatus.CONFIRMED);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));

        assertThatThrownBy(() -> service.acceptRegistration(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not pending");
    }

    @Test
    void acceptRegistration_tournamentFull_throwsException() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));
        when(registrationRepository.countByTournamentIdAndStatus(1L, RegistrationStatus.CONFIRMED))
                .thenReturn(8L);

        assertThatThrownBy(() -> service.acceptRegistration(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("full");
    }

    @Test
    void rejectRegistration_success() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));
        when(registrationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TournamentRegistrationDto result = service.rejectRegistration(1L);

        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.CANCELLED);
    }

    @Test
    void rejectRegistration_notPending_throwsException() {
        pendingRegistration.setStatus(RegistrationStatus.CONFIRMED);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));

        assertThatThrownBy(() -> service.rejectRegistration(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not pending");
    }

    // ── CANCEL ────────────────────────────────────────────────────────────────

    @Test
    void cancelRegistration_success() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));
        when(registrationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TournamentRegistrationDto result = service.cancelRegistration(1L);

        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.CANCELLED);
    }

    @Test
    void cancelRegistration_alreadyCancelled_throwsException() {
        pendingRegistration.setStatus(RegistrationStatus.CANCELLED);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));

        assertThatThrownBy(() -> service.cancelRegistration(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already cancelled");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void deleteRegistration_success() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));

        service.deleteRegistration(1L);

        verify(registrationRepository).delete(pendingRegistration);
    }

    @Test
    void deleteRegistration_notFound_throwsException() {
        when(registrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteRegistration(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── GET ───────────────────────────────────────────────────────────────────

    @Test
    void getRegistrationById_success() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(pendingRegistration));

        TournamentRegistrationDto result = service.getRegistrationById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getRegistrationById_notFound_throwsException() {
        when(registrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRegistrationById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Registration not found");
    }

    @Test
    void getTeamRegistrationsByPlayer_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(player));
        when(registrationRepository.findTeamRegistrationsByPlayerId(1L))
                .thenReturn(List.of());

        List<TournamentRegistrationDto> result = service.getTeamRegistrationsByPlayer(1L);

        assertThat(result).isNotNull();
        verify(registrationRepository).findTeamRegistrationsByPlayerId(1L);
    }
}
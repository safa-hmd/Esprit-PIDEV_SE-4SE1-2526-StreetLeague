package com.example.streetleague;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.FieldRepository;
import com.example.streetleague.Repository.TournamentRepository;
import com.example.streetleague.ServiceImp.TournamentServiceImp;
import com.example.streetleague.dto.TournamentDto;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImpTest {

    @Mock  TournamentRepository tournamentRepository;
    @Mock
    FieldRepository fieldRepository;
    @InjectMocks
    TournamentServiceImp service;

    private TournamentDto validDto;
    private Tournament savedTournament;
    private Field field;

    @BeforeEach
    void setUp() {
        field = Field.builder()
                .id(1L)
                .name("Terrain Ariana")
                .location("Ariana")
                .sportType(SportType.FOOTBALL)
                .build();

        validDto = TournamentDto.builder()
                .name("Summer Cup 2026")
                .sportType(SportType.FOOTBALL)
                .tournamentType(TournamentType.TEAM)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(20))
                .registrationDeadline(LocalDate.now().plusDays(5))
                .maxParticipants(8)
                .fieldId(1L)
                .build();



        savedTournament = Tournament.builder()
                .id(1L)
                .name("Summer Cup 2026")
                .sportType(SportType.FOOTBALL)
                .tournamentType(TournamentType.TEAM)
                .status(TournamentStatus.UPCOMING)
                .startDate(validDto.getStartDate())
                .endDate(validDto.getEndDate())
                .registrationDeadline(validDto.getRegistrationDeadline())
                .maxParticipants(8)
                .field(field)           // ✅
                .registrations(List.of())
                .build();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    void createTournament_success() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(tournamentRepository.existsByNameIgnoreCase("Summer Cup 2026")).thenReturn(false);
        when(tournamentRepository.save(any())).thenReturn(savedTournament);

        TournamentDto result = service.createTournament(validDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Summer Cup 2026");
        assertThat(result.getStatus()).isEqualTo(TournamentStatus.UPCOMING);
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void createTournament_duplicateName_throwsException() {
        when(tournamentRepository.existsByNameIgnoreCase("Summer Cup 2026")).thenReturn(true);

        assertThatThrownBy(() -> service.createTournament(validDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void createTournament_endDateBeforeStartDate_throwsException() {
        validDto.setEndDate(validDto.getStartDate().minusDays(1));

        assertThatThrownBy(() -> service.createTournament(validDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End date must be after start date");
    }

    @Test
    void createTournament_deadlineAfterStartDate_throwsException() {
        validDto.setRegistrationDeadline(validDto.getStartDate().plusDays(1));

        assertThatThrownBy(() -> service.createTournament(validDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registration deadline must be before start date");
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Test
    void getTournamentById_success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(savedTournament));

        TournamentDto result = service.getTournamentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Summer Cup 2026");
    }

    @Test
    void getTournamentById_notFound_throwsException() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTournamentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tournament not found");
    }

    @Test
    void getAllTournaments_returnsList() {
        when(tournamentRepository.findAll()).thenReturn(List.of(savedTournament));

        List<TournamentDto> result = service.getAllTournaments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Summer Cup 2026");
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    void updateTournament_success() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(savedTournament));
        when(tournamentRepository.save(any())).thenReturn(savedTournament);

        validDto.setName("Updated Cup");
        TournamentDto result = service.updateTournament(1L, validDto);

        assertThat(result).isNotNull();
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void updateTournament_cancelled_throwsException() {
        savedTournament.setStatus(TournamentStatus.CANCELLED);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(savedTournament));

        assertThatThrownBy(() -> service.updateTournament(1L, validDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot update a cancelled tournament");
    }

    // ── CANCEL ────────────────────────────────────────────────────────────────

    @Test
    void cancelTournament_success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(savedTournament));
        when(tournamentRepository.save(any())).thenReturn(savedTournament);

        TournamentDto result = service.cancelTournament(1L);

        assertThat(result).isNotNull();
        verify(tournamentRepository).save(argThat(t ->
                t.getStatus() == TournamentStatus.CANCELLED
        ));
    }

    @Test
    void cancelTournament_alreadyCancelled_throwsException() {
        savedTournament.setStatus(TournamentStatus.CANCELLED);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(savedTournament));

        assertThatThrownBy(() -> service.cancelTournament(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already cancelled");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void deleteTournament_success() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(savedTournament));

        service.deleteTournament(1L);

        verify(tournamentRepository).delete(savedTournament);
    }

    @Test
    void deleteTournament_notFound_throwsException() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTournament(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

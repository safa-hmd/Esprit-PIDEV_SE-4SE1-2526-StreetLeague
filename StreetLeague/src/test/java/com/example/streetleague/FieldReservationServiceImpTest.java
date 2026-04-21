package com.example.streetleague;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.FieldRepository;
import com.example.streetleague.Repository.FieldReservationRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceImp.FieldReservationServiceImp;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.FieldReservationDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldReservationServiceImpTest {

    @Mock FieldReservationRepository reservationRepository;
    @Mock FieldRepository fieldRepository;
    @Mock UserRepository userRepository;
    @InjectMocks FieldReservationServiceImp service;

    private Field field;
    private User player;
    private FieldReservation reservation;
    private FieldReservationDto validDto;

    private final LocalDateTime START = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
    private final LocalDateTime END   = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0);

    @BeforeEach
    void setUp() {
        field = Field.builder()
                .id(1L)
                .name("Central Court")
                .location("Tunis")
                .pricePerHour(80.0)
                .available(true)
                .build();

        player = new User();
        player.setIdUser(1L);
        player.setFullName("Ali Ben");

        reservation = FieldReservation.builder()
                .id(1L)
                .field(field)
                .player(player)
                .startTime(START)
                .endTime(END)
                .status(ReservationStatus.PENDING)
                .totalPrice(160.0)
                .build();

        validDto = FieldReservationDto.builder()
                .fieldId(1L)
                .playerId(1L)
                .startTime(START)
                .endTime(END)
                .build();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    void createReservation_success() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(userRepository.findById(1L)).thenReturn(Optional.of(player));
        when(reservationRepository.findConflictingReservations(any(), any(), any()))
                .thenReturn(List.of());
        when(reservationRepository.save(any())).thenReturn(reservation);

        FieldReservationDto result = service.createReservation(validDto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(result.getTotalPrice()).isEqualTo(160.0);
        verify(reservationRepository).save(any(FieldReservation.class));
    }

    @Test
    void createReservation_startTimeInPast_throwsException() {
        validDto.setStartTime(LocalDateTime.now().minusHours(1));
        validDto.setEndTime(LocalDateTime.now().plusHours(1));

        assertThatThrownBy(() -> service.createReservation(validDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start time must be in the future");
    }

    @Test
    void createReservation_endBeforeStart_throwsException() {
        validDto.setEndTime(START.minusHours(1));

        assertThatThrownBy(() -> service.createReservation(validDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("End time must be after start time");
    }

    @Test
    void createReservation_fieldNotAvailable_throwsException() {
        field.setAvailable(false);
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));

        assertThatThrownBy(() -> service.createReservation(validDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void createReservation_slotConflict_throwsException() {
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(reservationRepository.findConflictingReservations(any(), any(), any()))
                .thenReturn(List.of(reservation));

        assertThatThrownBy(() -> service.createReservation(validDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    void createReservation_fieldNotFound_throwsException() {
        when(fieldRepository.findById(99L)).thenReturn(Optional.empty());
        validDto.setFieldId(99L);

        assertThatThrownBy(() -> service.createReservation(validDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Field not found");
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Test
    void getReservationById_success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        FieldReservationDto result = service.getReservationById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void getReservationById_notFound_throwsException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReservationById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation not found");
    }

    @Test
    void getPendingReservations_returnsList() {
        when(reservationRepository.findByStatus(ReservationStatus.PENDING))
                .thenReturn(List.of(reservation));

        List<FieldReservationDto> result = service.getPendingReservations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    // ── APPROVE ───────────────────────────────────────────────────────────────

    @Test
    void approveReservation_success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FieldReservationDto result = service.approveReservation(1L, null);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.APPROVED);
    }

    @Test
    void approveReservation_notPending_throwsException() {
        reservation.setStatus(ReservationStatus.APPROVED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.approveReservation(1L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only pending reservations can be approved");
    }

    // ── REJECT ────────────────────────────────────────────────────────────────

    @Test
    void rejectReservation_success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FieldReservationDto result = service.rejectReservation(1L, null);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.REJECTED);
    }

    @Test
    void rejectReservation_notPending_throwsException() {
        reservation.setStatus(ReservationStatus.APPROVED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.rejectReservation(1L, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only pending reservations can be rejected");
    }

    // ── CANCEL ────────────────────────────────────────────────────────────────

    @Test
    void cancelReservation_success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FieldReservationDto result = service.cancelReservation(1L, 1L);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void cancelReservation_wrongPlayer_throwsException() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.cancelReservation(1L, 99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("your own reservations");
    }

    @Test
    void cancelReservation_alreadyCancelled_throwsException() {
        reservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.cancelReservation(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    void cancelReservation_rejected_throwsException() {
        reservation.setStatus(ReservationStatus.REJECTED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.cancelReservation(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel a rejected reservation");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void deleteReservation_success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        service.deleteReservation(1L);

        verify(reservationRepository).delete(reservation);
    }

    @Test
    void deleteReservation_notFound_throwsException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteReservation(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
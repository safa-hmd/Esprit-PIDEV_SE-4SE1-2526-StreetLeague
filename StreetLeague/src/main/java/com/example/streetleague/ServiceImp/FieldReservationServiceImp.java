package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.FieldRepository;
import com.example.streetleague.Repository.FieldReservationRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.IFieldReservationService;
import com.example.streetleague.Entity.Field;
import com.example.streetleague.Entity.FieldReservation;
import com.example.streetleague.Entity.ReservationStatus;
import com.example.streetleague.ServiceInterface.IPaymentService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.FieldReservationDto;
import com.example.streetleague.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FieldReservationServiceImp implements IFieldReservationService {

    private final FieldReservationRepository reservationRepository;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final IPaymentService paymentService;

    @Override
    public FieldReservationDto createReservation(FieldReservationDto dto) {
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time must be in the future");
        }
        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Field field = findFieldById(dto.getFieldId());

        if (!field.isAvailable()) {
            throw new IllegalStateException("This field is not available for reservation");
        }

        // Vérification conflit de créneau
        List<FieldReservation> conflicts = reservationRepository
                .findConflictingReservations(dto.getFieldId(), dto.getStartTime(), dto.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("This time slot is already booked for this field");
        }

        User player = findUserById(dto.getPlayerId());

        // Calcul du prix total
        long hours = java.time.Duration.between(dto.getStartTime(), dto.getEndTime()).toHours();
        Double totalPrice = field.getPricePerHour() != null ? hours * field.getPricePerHour() : null;

        FieldReservation reservation = FieldReservation.builder()
                .field(field)
                .player(player)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(ReservationStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        return mapToDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public FieldReservationDto getReservationById(Long id) {
        return mapToDto(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldReservationDto> getReservationsByPlayer(Long playerId) {
        return reservationRepository.findByPlayerIdUserOrderByCreatedAtDesc(playerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldReservationDto> getReservationsByField(Long fieldId) {
        return reservationRepository.findByFieldId(fieldId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldReservationDto> getPendingReservations() {
        return reservationRepository.findByStatus(ReservationStatus.PENDING).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FieldReservationDto approveReservation(Long id, String adminNote) {
        FieldReservation reservation = findById(id);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be approved");
        }

        reservation.setStatus(ReservationStatus.APPROVED);


        FieldReservation saved = reservationRepository.save(reservation);

        // déclenchement automatique du paiement
        paymentService.initiatePayment(saved.getId());

        return mapToDto(saved);
    }

    @Override
    public FieldReservationDto rejectReservation(Long id, String adminNote) {
        FieldReservation reservation = findById(id);
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be rejected");
        }
        reservation.setStatus(ReservationStatus.REJECTED);

        return mapToDto(reservationRepository.save(reservation));
    }

    @Override
    public FieldReservationDto cancelReservation(Long id, Long playerId) {
        FieldReservation reservation = findById(id);
        if (!reservation.getPlayer().getIdUser().equals(playerId)) {
            throw new IllegalStateException("You can only cancel your own reservations");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }
        if (reservation.getStatus() == ReservationStatus.REJECTED) {
            throw new IllegalStateException("Cannot cancel a rejected reservation");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return mapToDto(reservationRepository.save(reservation));
    }

    @Override
    public void deleteReservation(Long id) {
        FieldReservation reservation = findById(id);
        reservationRepository.delete(reservation);
    }

    // ---- Helpers ----

    private FieldReservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    private Field findFieldById(Long id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // Dans FieldReservationServiceImp, avant de sauvegarder
    private void validateReservationTimes(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        long hours = Duration.between(start, end).toHours();
        if (hours < 1) {
            throw new IllegalArgumentException("Minimum reservation duration is 1 hour");
        }
        if (hours > 12) {
            throw new IllegalArgumentException("Maximum reservation duration is 12 hours");
        }
    }

    private FieldReservationDto mapToDto(FieldReservation r) {
        return FieldReservationDto.builder()
                .id(r.getId())
                .fieldId(r.getField().getId())
                .fieldName(r.getField().getName())
                .fieldLocation(r.getField().getLocation())
                .playerId(r.getPlayer().getIdUser())
                .playerUsername(r.getPlayer().getFullName())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())

                .status(r.getStatus())
                .totalPrice(r.getTotalPrice())

                .createdAt(r.getCreatedAt())
                .build();
    }

}
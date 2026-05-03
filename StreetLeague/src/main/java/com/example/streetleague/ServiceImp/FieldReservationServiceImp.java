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
import java.util.Optional;
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
        return new FieldReservationDto();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FieldReservationDto> getReservationById(Long id) {
        return reservationRepository.findById(id).map(this::mapToDto);
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
    public List<FieldReservationDto> getReservationsByUserId(Long userId) {
        return reservationRepository.findByPlayerIdUserOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldReservationDto> getReservationsByFieldId(Long fieldId) {
        return reservationRepository.findByFieldId(fieldId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldReservationDto> getReservationsByStatus(String status) {
        ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
        return reservationRepository.findByStatus(reservationStatus).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FieldReservationDto updateReservation(Long id, FieldReservationDto reservationDto) {
        FieldReservation reservation = findById(id);
        
        // Update fields from DTO
        reservation.setStartTime(reservationDto.getStartTime());
        reservation.setEndTime(reservationDto.getEndTime());
        reservation.setTotalPrice(reservationDto.getTotalPrice());
        if (reservationDto.getStatus() != null) {
            reservation.setStatus(ReservationStatus.valueOf(reservationDto.getStatus().toUpperCase()));
        }
        
        FieldReservation saved = reservationRepository.save(reservation);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        FieldReservation reservation = findById(id);
        reservationRepository.delete(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFieldAvailable(Long fieldId, LocalDateTime startTime, LocalDateTime endTime) {
        List<FieldReservation> conflicts = reservationRepository
                .findConflictingReservations(fieldId, startTime, endTime);
        return conflicts.isEmpty();
    }

                
    // ---- Helpers ----

    private FieldReservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
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
        FieldReservationDto dto = new FieldReservationDto();
        dto.setId(r.getId());
        dto.setFieldId(r.getField().getId());
        dto.setFieldName(r.getField().getName());
        dto.setFieldLocation(r.getField().getLocation());
        dto.setPlayerId(r.getPlayer().getIdUser());
        dto.setUserName(r.getPlayer().getFullName());
        dto.setStartTime(r.getStartTime());
        dto.setEndTime(r.getEndTime());
        dto.setStatus(r.getStatus() != null ? r.getStatus().toString() : null);
        dto.setTotalPrice(r.getTotalPrice());
        dto.setReservationDate(r.getCreatedAt());
        return dto;
    }

    
}
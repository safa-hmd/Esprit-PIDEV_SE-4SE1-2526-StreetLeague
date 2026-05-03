package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.FieldReservationDto;
import java.util.List;
import java.util.Optional;

public interface IFieldReservationService {
    List<FieldReservationDto> getAllReservations();
    Optional<FieldReservationDto> getReservationById(Long id);
    FieldReservationDto createReservation(FieldReservationDto reservationDto);
    FieldReservationDto updateReservation(Long id, FieldReservationDto reservationDto);
    void deleteReservation(Long id);
    List<FieldReservationDto> getReservationsByUserId(Long userId);
    List<FieldReservationDto> getReservationsByFieldId(Long fieldId);
    List<FieldReservationDto> getReservationsByStatus(String status);
    boolean isFieldAvailable(Long fieldId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
}

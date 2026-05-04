package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.FieldReservationDto;

import java.util.List;

public interface IFieldReservationService {
    FieldReservationDto createReservation(FieldReservationDto dto);
    FieldReservationDto getReservationById(Long id);
    List<FieldReservationDto> getAllReservations();
    List<FieldReservationDto> getReservationsByPlayer(Long playerId);
    List<FieldReservationDto> getReservationsByField(Long fieldId);
    List<FieldReservationDto> getPendingReservations();
    FieldReservationDto approveReservation(Long id, String adminNote);
    FieldReservationDto rejectReservation(Long id, String adminNote);
    FieldReservationDto cancelReservation(Long id, Long playerId);
    void deleteReservation(Long id);
}
package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.Accommodation;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.dto.AccommodationDto;
import com.example.streetleague.dto.AccommodationRequestResponseDto;
import com.example.streetleague.dto.DecisionDto;
import com.example.streetleague.dto.TransportDto;
import com.example.streetleague.dto.TravelRequestResponseDto;

import java.util.List;

public interface AdminTravelService {

    Transport addTransport(TransportDto transportDto);
    List<TransportDto> getAllTransports();
    Transport updateTransport(Long id, TransportDto dto);
    void deleteTransport(Long id);

    Accommodation addAccommodation(AccommodationDto accommodationDto);
    List<TravelRequestResponseDto> getAllRequests();
    TravelRequestResponseDto decideRequest(Long requestId, DecisionDto decisionDto);

    List<AccommodationDto> getAllAccommodations();
    Accommodation updateAccommodation(Long id, AccommodationDto dto);
    void deleteAccommodation(Long id);
    Accommodation approveAccommodation(Long id);
    Accommodation rejectAccommodation(Long id);
    
    // Accommodation Request methods
    List<AccommodationRequestResponseDto> getAllAccommodationRequests();
    AccommodationRequestResponseDto approveAccommodationRequest(Long id, DecisionDto decision);
    AccommodationRequestResponseDto rejectAccommodationRequest(Long id, DecisionDto decision);
    byte[] generateRequestPdf(Long id);
}

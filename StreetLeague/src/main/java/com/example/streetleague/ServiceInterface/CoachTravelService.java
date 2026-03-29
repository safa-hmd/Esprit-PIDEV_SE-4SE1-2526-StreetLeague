package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.Accommodation;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TransportDto;
import com.example.streetleague.dto.TransportEligibilityResponseDto;
import com.example.streetleague.dto.TravelRequestDto;
import com.example.streetleague.dto.TravelRequestResponseDto;

import java.util.List;

public interface CoachTravelService {
    TransportEligibilityResponseDto checkEligibilityAndGetTransports(Long coachId, Long tournamentId);
    TransportDto submitPersonalCar(TransportDto carDto);

    List<Transport> getAvailableTransports(); // Original method if still needed
    List<Accommodation> getAvailableAccommodations();
    TravelRequestResponseDto submitTravelRequest(TravelRequestDto requestDto);
    List<TravelRequestResponseDto> getRequestsByTeam(Long teamId);
    List<User> getMyTeamMembers(Long coachId) ;
    List<Accommodation> getApprovedAccommodations();
    com.example.streetleague.dto.AccommodationRequestResponseDto submitAccommodationRequest(com.example.streetleague.dto.AccommodationRequestDto requestDto);
    List<com.example.streetleague.dto.AccommodationRequestResponseDto> getMyAccommodationRequests(Long coachId);
}

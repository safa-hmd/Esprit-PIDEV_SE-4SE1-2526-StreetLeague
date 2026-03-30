package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Accommodation;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Entity.Tournament;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.Entity.TravelRequest;
import com.example.streetleague.Entity.AccommodationRequest;
import com.example.streetleague.Repository.AccommodationRepository;
import com.example.streetleague.Repository.AccommodationRequestRepository;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.TournamentRepository;
import com.example.streetleague.Repository.TransportRepository;
import com.example.streetleague.Repository.TravelRequestRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.CoachTravelService;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.AccommodationDto;
import com.example.streetleague.dto.AccommodationRequestDto;
import com.example.streetleague.dto.AccommodationRequestResponseDto;
import com.example.streetleague.dto.TournamentDto;
import com.example.streetleague.dto.TransportDto;
import com.example.streetleague.dto.TransportEligibilityResponseDto;
import com.example.streetleague.dto.TravelRequestDto;
import com.example.streetleague.dto.TravelRequestResponseDto;
import com.example.streetleague.exception.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoachTravelServiceImpl implements CoachTravelService {

    private final TravelRequestRepository travelRequestRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final TransportRepository transportRepository;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationRequestRepository accommodationRequestRepository;
    private final UserRepository userRepository;

    public CoachTravelServiceImpl(TravelRequestRepository travelRequestRepository,
                                   TeamRepository teamRepository,
                                   TournamentRepository tournamentRepository,
                                   TransportRepository transportRepository,
                                   AccommodationRepository accommodationRepository,
                                   AccommodationRequestRepository accommodationRequestRepository,
                                   UserRepository userRepository) {
        this.travelRequestRepository = travelRequestRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
        this.transportRepository = transportRepository;
        this.accommodationRepository = accommodationRepository;
        this.accommodationRequestRepository = accommodationRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Transport> getAvailableTransports() {
        return transportRepository.findAll();
    }

    @Override
    public TransportEligibilityResponseDto checkEligibilityAndGetTransports(Long coachId, Long tournamentId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new BusinessValidationException("Coach not found"));
        
        Long teamId = coach.getTeamId();
        if (teamId == null) {
            List<Team> captained = teamRepository.findByCaptain_IdUser(coachId);
            if (captained != null && !captained.isEmpty()) {
                teamId = captained.get(0).getIdTeam();
            }
        }
        
        if (teamId == null) {
            throw new BusinessValidationException("Coach has no assigned team");
        }
        
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessValidationException("Team not found"));
        
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new BusinessValidationException("Tournament not found"));

        boolean isSameCity = team.getCity() != null 
                          && tournament.getCity() != null 
                          && team.getCity().equalsIgnoreCase(tournament.getCity());

        if (isSameCity) {
            return TransportEligibilityResponseDto.builder()
                    .eligible(false)
                    .reason("Team and Tournament are in the same city. No transport required.")
                    .transports(new ArrayList<>())
                    .build();
        }

        List<TransportDto> transports = transportRepository.findAll().stream()
                .map(t -> TransportDto.builder()
                        .id(t.getId())
                        .type(t.getType())
                        .destination(t.getDestination())
                        .availableSeats(t.getAvailableSeats())
                        .pricePerSeat(t.getPricePerSeat())
                        .departureTime(t.getDepartureTime())
                        .returnTime(t.getReturnTime())
                        .build())
                .collect(Collectors.toList());

        return TransportEligibilityResponseDto.builder()
                .eligible(true)
                .reason("Eligible for transport.")
                .transports(transports)
                .build();
    }

    @Override
    @Transactional
    public TransportDto submitPersonalCar(TransportDto carDto) {
        System.out.println("DEBUG [CoachTravelService] submitPersonalCar coachId received: " + carDto.getCoachId());
        Transport transport = Transport.builder()
                .type(com.example.streetleague.Entity.TransportType.PRIVATE_CAR)
                .pricePerSeat(carDto.getPricePerSeat())
                .availableSeats(carDto.getAvailableSeats() != null ? carDto.getAvailableSeats() : 0)
                .departureTime(carDto.getDepartureTime())
                .returnTime(carDto.getReturnTime())
                .destination(carDto.getDestination())
                .status("PENDING")
                .coachId(carDto.getCoachId())
                .build();
        
        Transport saved = transportRepository.save(transport);

        return TransportDto.builder()
                .id(saved.getId())
                .type(saved.getType())
                .destination(saved.getDestination())
                .availableSeats(saved.getAvailableSeats())
                .pricePerSeat(saved.getPricePerSeat())
                .departureTime(saved.getDepartureTime())
                .returnTime(saved.getReturnTime())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public List<Accommodation> getAvailableAccommodations() {
        return accommodationRepository.findAll();
    }

    @Override
    public List<Accommodation> getApprovedAccommodations() {
        return accommodationRepository.findByStatus("APPROVED");
    }

    @Override
    @Transactional
    public AccommodationRequestResponseDto submitAccommodationRequest(
            AccommodationRequestDto requestDto) {

        Accommodation accommodation = accommodationRepository
                .findById(requestDto.getAccommodationId())
                .orElseThrow(() -> new BusinessValidationException(
                        "Accommodation not found"));

        AccommodationRequest request = AccommodationRequest.builder()
                .accommodation(accommodation)
                .memberIds(requestDto.getMemberIds())
                .tournamentId(requestDto.getTournamentId())
                .coachId(requestDto.getCoachId() != null
                        ? requestDto.getCoachId() : 1L)
                .coachName(requestDto.getCoachName())
                .totalAmount(requestDto.getTotalAmount())
                .build();

        AccommodationRequest saved =
                accommodationRequestRepository.save(request);

        return buildAccommodationRequestResponse(saved, accommodation);
    }

    @Override
    public List<AccommodationRequestResponseDto> getMyAccommodationRequests(
            Long coachId) {

        List<AccommodationRequestResponseDto> result = new ArrayList<>();
        for (AccommodationRequest saved :
                accommodationRequestRepository.findByCoachId(coachId)) {
            result.add(buildAccommodationRequestResponse(
                    saved, saved.getAccommodation()));
        }
        return result;
    }

    private AccommodationRequestResponseDto buildAccommodationRequestResponse(
            AccommodationRequest saved, Accommodation accommodation) {

        return AccommodationRequestResponseDto.builder()
                .id(saved.getId())
                .accommodation(AccommodationDto.builder()
                        .id(accommodation.getId())
                        .type(accommodation.getType())
                        .numberOfNights(accommodation.getNumberOfNights())
                        .pricePerNight(accommodation.getPricePerNight())
                        .address(accommodation.getAddress())
                        .formula(accommodation.getFormula())
                        .capacity(accommodation.getCapacity())
                        .status(accommodation.getStatus())
                        .build())
                .memberIds(saved.getMemberIds())
                .tournamentId(saved.getTournamentId())
                .coachId(saved.getCoachId())
                .coachName(saved.getCoachName())
                .status(saved.getStatus())
                .adminComment(saved.getAdminComment())
                .createdAt(saved.getCreatedAt())
                .totalAmount(saved.getTotalAmount())
                .build();
    }

    @Override
    public List<User> getMyTeamMembers(Long coachId) {
        System.out.println("=== getMyTeamMembers coachId: " + coachId);

        Long teamId = resolveTeamId(coachId);

        if (teamId == null) {
            System.out.println("No teamId for coachId: " + coachId);
            return new ArrayList<>();
        }

        System.out.println("Loading players for teamId: " + teamId);
        // Find all users where team_id = that team_id AND role = 'PLAYER'
        List<User> players = userRepository.findByTeamIdAndRole(teamId, Role.PLAYER);
        System.out.println("Players found by junction: " + players.size());
        
        // Final fallback: if no players found, try searching by team_id directly from teams table
        if (players.isEmpty()) {
            System.out.println("Junction returned empty, trying fallback by teamId field...");
            players = userRepository.findByTeamId(teamId).stream()
                    .filter(u -> u.getRole() == Role.PLAYER)
                    .collect(Collectors.toList());
            System.out.println("Players found by fallback: " + players.size());
        }
        
        return players;
    }

    @Override
    public Long resolveTeamId(Long coachId) {
        if (coachId == null) return null;
        
        // 1. Check junction table (if coach is also a player or explicitly joined)
        Long teamId = userRepository.findTeamIdByUserId(coachId);
        
        // 2. Check if coach is a captain of any team
        if (teamId == null) {
            List<Team> captained = teamRepository.findByCaptain_IdUser(coachId);
            if (captained != null && !captained.isEmpty()) {
                teamId = captained.get(0).getIdTeam();
            }
        }
        
        // 3. Last fallback: Check team_id field on user if it exists
        if (teamId == null) {
            try {
                User user = userRepository.findById(coachId).orElse(null);
                if (user != null) {
                    teamId = user.getTeamId();
                }
            } catch (Exception e) {}
        }
        
        return teamId;
    }

    @Override
    @Transactional
    public TravelRequestResponseDto submitTravelRequest(
            TravelRequestDto requestDto) {

        Long teamId = requestDto.getTeamId();
        if (teamId == null) {
            teamId = resolveTeamId(requestDto.getCoachId());
        }
        
        if (teamId == null) {
            throw new BusinessValidationException("Could not resolve team for coach ID: " + requestDto.getCoachId());
        }
        
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessValidationException("Team not found"));

        Tournament tournament = tournamentRepository
                .findById(requestDto.getTournamentId())
                .orElseThrow(() -> new BusinessValidationException(
                        "Tournament not found"));

        Transport transport = null;
        if (requestDto.getTransportId() != null) {
            transport = transportRepository
                    .findById(requestDto.getTransportId())
                    .orElseThrow(() -> new BusinessValidationException(
                            "Transport not found"));
        }

        Accommodation accommodation = null;
        if (requestDto.getAccommodationId() != null) {
            accommodation = accommodationRepository
                    .findById(requestDto.getAccommodationId())
                    .orElseThrow(() -> new BusinessValidationException(
                            "Accommodation not found"));
        }

        boolean isSameCity = team.getCity() != null
                && tournament.getCity() != null
                && team.getCity().equalsIgnoreCase(tournament.getCity());

        if (isSameCity && transport != null) {
            throw new BusinessValidationException(
                    "Transport not allowed when team is in same city.");
        }
        if (isSameCity && accommodation == null) {
            throw new BusinessValidationException(
                    "Accommodation required when team is in same city.");
        }

        int paxCount = (requestDto.getSelectedMemberIds() != null) ? requestDto.getSelectedMemberIds().size() : 0;
        
        if (requestDto.getSelectedMemberIds() == null || requestDto.getSelectedMemberIds().isEmpty()) {
            throw new BusinessValidationException("CRITICAL BACKEND CHECK: The backend received 0 members from the frontend! The selectedMemberIds array is empty or null parsing JSON. Payload transportId: " + requestDto.getTransportId());
        }

        if (transport != null) {
            if (transport.getAvailableSeats() != null && transport.getAvailableSeats() < paxCount) {
                throw new BusinessValidationException("Not enough seats available on transport.");
            }
            // Optional: Reduce seats now or later. If we reduce seats now:
            if (transport.getAvailableSeats() != null) {
                transport.setAvailableSeats(transport.getAvailableSeats() - paxCount);
                transportRepository.save(transport);
            }
        }

        double totalAmount = 0.0;
        if (transport != null && transport.getPricePerSeat() != null) {
            totalAmount += transport.getPricePerSeat() * paxCount;
        }
        if (accommodation != null
                && accommodation.getPricePerNight() != null
                && accommodation.getNumberOfNights() != null) {
            totalAmount += accommodation.getPricePerNight()
                    * accommodation.getNumberOfNights();
        }

        TravelRequest request = TravelRequest.builder()
                .team(team)
                .tournament(tournament)
                .transport(transport)
                .accommodation(accommodation)
                .sameCity(isSameCity)
                .accommodationRequired(isSameCity)
                .totalAmount(totalAmount)
                .selectedMemberIds(requestDto.getSelectedMemberIds())
                .build();

        return mapToResponseDto(travelRequestRepository.save(request));
    }

    @Override
    public List<TravelRequestResponseDto> getRequestsByTeam(Long teamId) {
        return travelRequestRepository.findByTeam_IdTeam(teamId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private TravelRequestResponseDto mapToResponseDto(TravelRequest req) {
        return TravelRequestResponseDto.builder()
                .id(req.getId())
                .teamId(req.getTeam().getIdTeam())
                .teamName(req.getTeam().getName())
                .teamCity(req.getTeam().getCity())
                .tournament(TournamentDto.builder()
                        .id(req.getTournament().getId())
                        .name(req.getTournament().getName())
                        .city(req.getTournament().getCity())
                        .startDate(req.getTournament().getStartDate())
                        .endDate(req.getTournament().getEndDate())
                        .build())
                .transport(req.getTransport() != null
                        ? TransportDto.builder()
                        .id(req.getTransport().getId())
                        .type(req.getTransport().getType())
                        .pricePerSeat(req.getTransport().getPricePerSeat())
                        .destination(req.getTransport().getDestination())
                        .departureTime(req.getTransport().getDepartureTime())
                        .returnTime(req.getTransport().getReturnTime())
                        .build()
                        : null)
                .accommodation(req.getAccommodation() != null
                        ? AccommodationDto.builder()
                        .id(req.getAccommodation().getId())
                        .type(req.getAccommodation().getType())
                        .formula(req.getAccommodation().getFormula())
                        .numberOfNights(req.getAccommodation().getNumberOfNights())
                        .pricePerNight(req.getAccommodation().getPricePerNight())
                        .build()
                        : null)
                .status(req.getStatus())
                .sameCity(req.getSameCity())
                .accommodationRequired(req.getAccommodationRequired())
                .adminComment(req.getAdminComment())
                .totalAmount(req.getTotalAmount())
                .individualPrice(req.getTransport() != null ? req.getTransport().getPricePerSeat() : null)
                .createdAt(req.getCreatedAt())
                .selectedMemberIds(req.getSelectedMemberIds())
                .build();
    }
}
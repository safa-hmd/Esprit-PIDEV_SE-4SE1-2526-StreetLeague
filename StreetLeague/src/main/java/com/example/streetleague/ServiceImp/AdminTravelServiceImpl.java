package com.example.streetleague.ServiceImp;
import java.util.ArrayList;
import com.example.streetleague.Entity.Accommodation;
import com.example.streetleague.Entity.AccommodationRequest;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.Entity.TravelRequest;
import com.example.streetleague.Repository.AccommodationRepository;
import com.example.streetleague.Repository.AccommodationRequestRepository;
import com.example.streetleague.Repository.TransportRepository;
import com.example.streetleague.Repository.TravelRequestRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.NotificationService;
import com.example.streetleague.ServiceInterface.AdminTravelService;
import com.example.streetleague.dto.AccommodationDto;
import com.example.streetleague.dto.AccommodationRequestResponseDto;
import com.example.streetleague.dto.DecisionDto;
import com.example.streetleague.dto.TournamentDto;
import com.example.streetleague.dto.TransportDto;
import com.example.streetleague.dto.TravelRequestResponseDto;
import com.example.streetleague.exception.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminTravelServiceImpl implements AdminTravelService {

    private final TransportRepository transportRepository;
    private final AccommodationRepository accommodationRepository;
    private final TravelRequestRepository travelRequestRepository;
    private final AccommodationRequestRepository accommodationRequestRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Accommodation addAccommodation(AccommodationDto accommodationDto) {
        System.out.println("=== ADD ACCOMMODATION DEBUG ===");
        System.out.println("type: " + accommodationDto.getType());
        System.out.println("numberOfNights: " + accommodationDto.getNumberOfNights());
        System.out.println("pricePerNight: " + accommodationDto.getPricePerNight());
        System.out.println("formula: " + accommodationDto.getFormula());
        System.out.println("address: " + accommodationDto.getAddress());

        Accommodation accommodation = Accommodation.builder()
                .type(accommodationDto.getType())
                .numberOfNights(accommodationDto.getNumberOfNights())
                .pricePerNight(accommodationDto.getPricePerNight())
                .address(accommodationDto.getAddress())
                .formula(accommodationDto.getFormula())
                .capacity(accommodationDto.getCapacity() != null ?accommodationDto.getCapacity() : 0)
                .build();

        return accommodationRepository.save(accommodation);
    }

    @Override
    public List<AccommodationDto> getAllAccommodations() {
        List<AccommodationDto> result = new ArrayList<>();
        for (Accommodation a : accommodationRepository.findAll()) {
            AccommodationDto dto = AccommodationDto.builder()
                    .id(a.getId())
                    .type(a.getType())
                    .numberOfNights(a.getNumberOfNights())
                    .pricePerNight(a.getPricePerNight())
                    .address(a.getAddress())
                    .formula(a.getFormula())
                    .capacity(a.getCapacity() != null ? a.getCapacity() : 0)
                    .status(a.getStatus() != null ? a.getStatus() : "PENDING")
                    .build();
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public Accommodation updateAccommodation(Long id, AccommodationDto dto) {
        Accommodation acc = accommodationRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Accommodation not found"));
        acc.setType(dto.getType());
        acc.setNumberOfNights(dto.getNumberOfNights());
        acc.setPricePerNight(dto.getPricePerNight());
        acc.setAddress(dto.getAddress());
        acc.setFormula(dto.getFormula());
        acc.setCapacity(dto.getCapacity());
        if (dto.getStatus() != null) {
            acc.setStatus(dto.getStatus());
        }
        return accommodationRepository.save(acc);
    }

    @Override
    @Transactional
    public void deleteAccommodation(Long id) {
        if (!accommodationRepository.existsById(id)) {
            throw new BusinessValidationException("Accommodation not found");
        }
        accommodationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Accommodation approveAccommodation(Long id) {
        Accommodation acc = accommodationRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Accommodation not found"));
        acc.setStatus("APPROVED");
        return accommodationRepository.save(acc);
    }

    @Override
    @Transactional
    public Accommodation rejectAccommodation(Long id) {
        Accommodation acc = accommodationRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Accommodation not found"));
        acc.setStatus("REJECTED");
        return accommodationRepository.save(acc);
    }

    @Override
    public List<TravelRequestResponseDto> getAllRequests() {
        return travelRequestRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TravelRequestResponseDto decideRequest(Long requestId, DecisionDto decisionDto) {
        TravelRequest request = travelRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessValidationException("Travel Request not found"));

        if (decisionDto.getStatus() != null) {
            request.setStatus(decisionDto.getStatus());
        }
        if (decisionDto.getAdminComment() != null) {
            request.setAdminComment(decisionDto.getAdminComment());
        }

        TravelRequest savedRequest = travelRequestRepository.save(request);
        return mapToResponseDto(savedRequest);
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
                .transport(req.getTransport() != null ? TransportDto.builder()
                        .id(req.getTransport().getId())
                        .type(req.getTransport().getType())
                        .pricePerSeat(req.getTransport().getPricePerSeat())
                        .destination(req.getTransport().getDestination())
                        .departureTime(req.getTransport().getDepartureTime())
                        .returnTime(req.getTransport().getReturnTime())
                        .build() : null)
                .accommodation(req.getAccommodation() != null ? AccommodationDto.builder()
                        .id(req.getAccommodation().getId())
                        .type(req.getAccommodation().getType())
                        .formula(req.getAccommodation().getFormula())
                        .numberOfNights(req.getAccommodation().getNumberOfNights())
                        .pricePerNight(req.getAccommodation().getPricePerNight())
                        .build() : null)
                .status(req.getStatus())
                .sameCity(req.getSameCity())
                .accommodationRequired(req.getAccommodationRequired())
                .adminComment(req.getAdminComment())
                .totalAmount(req.getTotalAmount())
                .createdAt(req.getCreatedAt())
                .selectedMemberIds(req.getSelectedMemberIds())
                .build();
    }

    @Override
    @Transactional
    public Transport addTransport(TransportDto transportDto) {
        Transport transport = Transport.builder()
                .type(transportDto.getType())
                .pricePerSeat(transportDto.getPricePerSeat())
                .availableSeats(transportDto.getAvailableSeats() != null
                        ? transportDto.getAvailableSeats() : 0)
                .departureTime(transportDto.getDepartureTime())
                .returnTime(transportDto.getReturnTime())
                .destination(transportDto.getDestination())
                .status("APPROVED") // Default for admin-added transports
                .build();
        return transportRepository.save(transport);
    }

    @Override
    public List<TransportDto> getAllTransports() {
        return transportRepository.findAll().stream()
                .map(t -> {
                    String coachName = "Official League";
                    if (t.getCoachId() != null) {
                        coachName = userRepository.findById(t.getCoachId())
                                .map(u -> u.getFullName() != null ? u.getFullName() : u.getEmail())
                                .orElse("Unknown Coach");
                    }
                    return TransportDto.builder()
                        .id(t.getId())
                        .type(t.getType())
                        .destination(t.getDestination())
                        .availableSeats(t.getAvailableSeats())
                        .pricePerSeat(t.getPricePerSeat())
                        .departureTime(t.getDepartureTime())
                        .returnTime(t.getReturnTime())
                        .status(t.getStatus())
                        .coachId(t.getCoachId())
                        .coachName(coachName)
                        .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Transport approveTransport(Long id) {
        Transport transport = transportRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Transport not found"));
        transport.setStatus("APPROVED");
        Transport saved = transportRepository.save(transport);
        
        if (saved.getCoachId() != null) {
            notificationService.sendNotification(
                saved.getCoachId(),
                "✅ Your personal vehicle submission for " + saved.getDestination() + " has been APPROVED. It is now available for booking."
            );
        }
        return saved;
    }

    @Override
    @Transactional
    public Transport rejectTransport(Long id) {
        Transport transport = transportRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Transport not found"));
        transport.setStatus("REJECTED");
        Transport saved = transportRepository.save(transport);
        
        if (saved.getCoachId() != null) {
            notificationService.sendNotification(
                saved.getCoachId(),
                "❌ Your personal vehicle submission for " + saved.getDestination() + " has been REJECTED by the administrator."
            );
        }
        return saved;
    }

    @Override
    @Transactional
    public Transport updateTransport(Long id, TransportDto dto) {
        Transport transport = transportRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Transport not found"));
        
        transport.setType(dto.getType());
        transport.setDestination(dto.getDestination());
        transport.setAvailableSeats(dto.getAvailableSeats() != null ? dto.getAvailableSeats() : 0);
        transport.setPricePerSeat(dto.getPricePerSeat());
        transport.setDepartureTime(dto.getDepartureTime());
        transport.setReturnTime(dto.getReturnTime());
        
        return transportRepository.save(transport);
    }

    @Override
    @Transactional
    public void deleteTransport(Long id) {
        if (!transportRepository.existsById(id)) {
            throw new BusinessValidationException("Transport not found");
        }
        transportRepository.deleteById(id);
    }

    // ======================== ACCOMMODATION REQUEST METHODS ========================

    @Override
    public List<AccommodationRequestResponseDto> getAllAccommodationRequests() {
        return accommodationRequestRepository.findAll()
            .stream()
            .map(this::mapToAccommodationRequestResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccommodationRequestResponseDto approveAccommodationRequest(
        Long id, DecisionDto decision) {
        AccommodationRequest req = 
            accommodationRequestRepository.findById(id)
            .orElseThrow(() -> new BusinessValidationException(
                "Request not found: " + id));
        req.setStatus("APPROVED");
        if (decision != null && decision.getAdminComment() != null) {
            req.setAdminComment(decision.getAdminComment());
        }
        AccommodationRequest saved = 
            accommodationRequestRepository.save(req);
            
        if (saved.getCoachId() != null) {
            notificationService.sendNotification(
                saved.getCoachId(),
                "✅ Your accommodation request for " + (saved.getAccommodation() != null ? saved.getAccommodation().getAddress() : "tournament") + " has been APPROVED."
            );
        }
        return mapToAccommodationRequestResponseDto(saved);
    }

    @Override
    @Transactional
    public AccommodationRequestResponseDto rejectAccommodationRequest(
        Long id, DecisionDto decision) {
        AccommodationRequest req = 
            accommodationRequestRepository.findById(id)
            .orElseThrow(() -> new BusinessValidationException(
                "Request not found: " + id));
        req.setStatus("REJECTED");
        if (decision != null && decision.getAdminComment() != null) {
            req.setAdminComment(decision.getAdminComment());
        }
        AccommodationRequest saved = 
            accommodationRequestRepository.save(req);
            
        if (saved.getCoachId() != null) {
            notificationService.sendNotification(
                saved.getCoachId(),
                "❌ Your accommodation request for " + (saved.getAccommodation() != null ? saved.getAccommodation().getAddress() : "tournament") + " has been REJECTED."
            );
        }
        return mapToAccommodationRequestResponseDto(saved);
    }

    @Override
    public byte[] generateRequestPdf(Long id) {
        return pdfGeneratorService.generateRequestPdf(id);
    }

    @Override
    public byte[] generateTransportPdf(Long id) {
        return pdfGeneratorService.generateTransportPdf(id);
    }

    @Override
    public byte[] generateTravelRequestPdf(Long id) {
        return pdfGeneratorService.generateTravelRequestPdf(id);
    }

    private AccommodationRequestResponseDto mapToAccommodationRequestResponseDto(AccommodationRequest req) {
        String coachName = req.getCoachName();
        if ((coachName == null || coachName.trim().isEmpty()) && req.getCoachId() != null) {
            coachName = userRepository.findById(req.getCoachId())
                    .map(u -> (u.getFullName() != null ? u.getFullName() : u.getEmail()))
                    .orElse("Official Coach");
        }

        return AccommodationRequestResponseDto.builder()
            .id(req.getId())
            .coachId(req.getCoachId())
            .coachName(coachName)
            .tournamentId(req.getTournamentId())
            .memberIds(req.getMemberIds())
            .totalAmount(req.getTotalAmount())
            .status(req.getStatus())
            .adminComment(req.getAdminComment())
            .createdAt(req.getCreatedAt() != null ? 
                req.getCreatedAt() : null)
            .accommodation(req.getAccommodation() != null ?
                AccommodationDto.builder()
                    .id(req.getAccommodation().getId())
                    .type(req.getAccommodation().getType())
                    .formula(req.getAccommodation().getFormula())
                    .numberOfNights(req.getAccommodation().getNumberOfNights())
                    .pricePerNight(req.getAccommodation().getPricePerNight())
                    .address(req.getAccommodation().getAddress())
                    .capacity(req.getAccommodation().getCapacity())
                    .status(req.getAccommodation().getStatus())
                    .build() : null)
            .build();
    }
}

package com.example.streetleague;

import com.example.streetleague.Entity.Accommodation;
import com.example.streetleague.Entity.AccommodationType;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.Entity.TransportType;
import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.AdminTravelServiceImpl;
import com.example.streetleague.ServiceImp.PdfGeneratorService;
import com.example.streetleague.ServiceInterface.NotificationService;
import com.example.streetleague.dto.AccommodationDto;
import com.example.streetleague.dto.TransportDto;
import com.example.streetleague.exception.BusinessValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTravelServiceImplTest {

    @InjectMocks
    private AdminTravelServiceImpl adminTravelService;

    @Mock private TransportRepository transportRepository;
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private TravelRequestRepository travelRequestRepository;
    @Mock private AccommodationRequestRepository accommodationRequestRepository;
    @Mock private PdfGeneratorService pdfGeneratorService;
    @Mock private NotificationService notificationService;

    private Accommodation accommodation;
    private Transport transport;

    @BeforeEach
    void setUp() {
        accommodation = Accommodation.builder()
                .id(1L)
                .type(AccommodationType.HOTEL)
                .address("Tunis")
                .pricePerNight(100.0)
                .numberOfNights(2)
                .capacity(50)
                .build();

        transport = Transport.builder()
                .id(1L)
                .type(TransportType.BUS)
                .destination("Sousse")
                .pricePerSeat(25.0)
                .availableSeats(50)
                .build();
    }

    @Test
    void addAccommodationTest() {
        AccommodationDto dto = AccommodationDto.builder()
                .type(AccommodationType.HOTEL)
                .address("Tunis")
                .pricePerNight(100.0)
                .numberOfNights(2)
                .capacity(50)
                .build();

        when(accommodationRepository.save(any(Accommodation.class))).thenReturn(accommodation);

        Accommodation result = adminTravelService.addAccommodation(dto);

        assertNotNull(result);
        assertEquals(AccommodationType.HOTEL, result.getType());
        verify(accommodationRepository, times(1)).save(any(Accommodation.class));
    }

    @Test
    void updateAccommodationTest() {
        AccommodationDto dto = AccommodationDto.builder()
                .type(AccommodationType.APARTMENT)
                .capacity(100)
                .build();

        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(any(Accommodation.class))).thenReturn(accommodation);

        Accommodation result = adminTravelService.updateAccommodation(1L, dto);

        assertNotNull(result);
        verify(accommodationRepository).save(any(Accommodation.class));
    }

    @Test
    void deleteAccommodation_NotFound_ThrowsException() {
        when(accommodationRepository.existsById(99L)).thenReturn(false);

        assertThrows(BusinessValidationException.class, () -> {
            adminTravelService.deleteAccommodation(99L);
        });
    }

    @Test
    void addTransportTest() {
        TransportDto dto = TransportDto.builder()
                .type(TransportType.BUS)
                .destination("Sousse")
                .pricePerSeat(25.0)
                .availableSeats(50)
                .build();

        when(transportRepository.save(any(Transport.class))).thenReturn(transport);

        Transport result = adminTravelService.addTransport(dto);

        assertNotNull(result);
        assertEquals(TransportType.BUS, result.getType());
        verify(transportRepository).save(any(Transport.class));
    }
}

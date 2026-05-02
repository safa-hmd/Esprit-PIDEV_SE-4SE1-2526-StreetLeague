package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Field;
import com.example.streetleague.Entity.FieldReservation;
import com.example.streetleague.Entity.ReservationStatus;
import com.example.streetleague.Repository.FieldRepository;
import com.example.streetleague.Repository.FieldReservationRepository;
import com.example.streetleague.dto.PricingRequest;
import com.example.streetleague.dto.PricingResponse;
import com.example.streetleague.dto.SuggestedPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

    private final FieldRepository fieldRepository;
    private final FieldReservationRepository reservationRepository;
    private final RestTemplate restTemplate;
    private final FieldReservationRepository fieldReservationRepository;

    @Value("${ml.flask.url:http://localhost:5000}")
    private String flaskUrl;

    // Fenêtre glissante pour le calcul du taux d'occupation (4 semaines)
    private static final int OCCUPATION_WINDOW_WEEKS = 4;
    // Créneaux dispo par semaine = 7j × 15h
    private static final double SLOTS_PER_WEEK = 105.0;

    public SuggestedPriceResponse getSuggestedPrice(Long fieldId, int durationHours) {

        // ── 1. Récupérer le terrain ────────────────────────────────
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Terrain introuvable : " + fieldId));

        // ── 2. Calculer les features temporelles ───────────────────
        LocalDateTime now        = LocalDateTime.now();
        int dayOfWeek            = now.getDayOfWeek().getValue() - 1; // 0=lun … 6=dim
        int hourOfDay            = now.getHour();
        int month                = now.getMonthValue();
        int isWeekend            = (now.getDayOfWeek() == DayOfWeek.SATURDAY
                || now.getDayOfWeek() == DayOfWeek.SUNDAY) ? 1 : 0;
        int isPeakHour           = (hourOfDay >= 18 && hourOfDay <= 22) ? 1 : 0;

        // ── 3. Calculer le taux d'occupation ───────────────────────
        LocalDateTime windowStart = now.minusWeeks(OCCUPATION_WINDOW_WEEKS);
        List<FieldReservation> recentReservations =
                fieldReservationRepository.findByFieldIdAndStartTimeAfter(fieldId, windowStart);

        long approvedCount = recentReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.APPROVED)
                .count();
        double occupationRate = Math.min(approvedCount / (OCCUPATION_WINDOW_WEEKS * SLOTS_PER_WEEK), 1.0);

        // ── 4. Calculer le taux d'annulation ───────────────────────
        List<FieldReservation> allReservations =
                reservationRepository.findByFieldId(fieldId);

        double cancellationRate = 0.0;
        if (!allReservations.isEmpty()) {
            long cancelled = allReservations.stream()
                    .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                    .count();
            cancellationRate = (double) cancelled / allReservations.size();
        }

        // ── 5. Construire la requête Flask ─────────────────────────
        PricingRequest flaskRequest = PricingRequest.builder()
                .sportType(field.getSportType().name())
                .location(field.getLocation())
                .capacity(field.getCapacity())
                .basePricePerHour(field.getPricePerHour())
                .durationHours(durationHours)
                .dayOfWeek(dayOfWeek)
                .hourOfDay(hourOfDay)
                .isWeekend(isWeekend)
                .isPeakHour(isPeakHour)
                .month(month)
                .occupationRate(Math.round(occupationRate * 1000.0) / 1000.0)
                .cancellationRate(Math.round(cancellationRate * 1000.0) / 1000.0)
                .build();

        log.info("Appel Flask /predict pour field={} | occupation={} | cancellation={}",
                fieldId, flaskRequest.getOccupationRate(), flaskRequest.getCancellationRate());

        // ── 6. Appel Flask ─────────────────────────────────────────
        PricingResponse flaskResponse;
        try {
            flaskResponse = restTemplate.postForObject(
                    flaskUrl + "/predict",
                    flaskRequest,
                    PricingResponse.class
            );
        } catch (Exception e) {
            log.error("Erreur appel Flask : {}", e.getMessage());
            // Fallback : retourner le prix de base si Flask est indisponible
            return SuggestedPriceResponse.builder()
                    .suggestedPrice(field.getPricePerHour())
                    .basePrice(field.getPricePerHour())
                    .deltaPercent(0.0)
                    .sport(field.getSportType().name())
                    .fieldName(field.getName())
                    .build();
        }

        // ── 7. Construire la réponse Angular ───────────────────────
        return SuggestedPriceResponse.builder()
                .suggestedPrice(flaskResponse.getSuggestedPrice())
                .basePrice(field.getPricePerHour())
                .deltaPercent(flaskResponse.getDeltaPercent())
                .sport(field.getSportType().name())
                .fieldName(field.getName())
                .build();
    }
    public SuggestedPriceResponse getSuggestedPriceFromParams(
            String sportType, String location, int capacity, int durationHours) {

        LocalDateTime now    = LocalDateTime.now();
        int dayOfWeek        = now.getDayOfWeek().getValue() - 1;
        int hourOfDay        = now.getHour();
        int month            = now.getMonthValue();
        int isWeekend        = (now.getDayOfWeek() == DayOfWeek.SATURDAY
                || now.getDayOfWeek() == DayOfWeek.SUNDAY) ? 1 : 0;
        int isPeakHour       = (hourOfDay >= 18 && hourOfDay <= 22) ? 1 : 0;

        // Prix de base selon sport
        double basePrice =  switch (sportType) {
            case "PADEL"      -> 50.0;
            case "TENNIS"     -> 40.0;
            case "FOOTBALL"   -> 30.0;
            case "BASKETBALL" -> 25.0;
            case "VOLLEYBALL" -> 20.0;
            default           -> 20.0;
        };

        PricingRequest req = PricingRequest.builder()
                .sportType(sportType)
                .location(location)
                .capacity(capacity)
                .basePricePerHour(basePrice)
                .durationHours(durationHours)
                .dayOfWeek(dayOfWeek)
                .hourOfDay(hourOfDay)
                .isWeekend(isWeekend)
                .isPeakHour(isPeakHour)
                .month(month)
                .occupationRate(0.5)       // taux neutre pour un nouveau terrain
                .cancellationRate(0.1)
                .build();

        PricingResponse flaskResponse;
        try {
            flaskResponse = restTemplate.postForObject(
                    flaskUrl + "/predict", req, PricingResponse.class);
        } catch (Exception e) {
            log.error("Flask indisponible : {}", e.getMessage());
            return SuggestedPriceResponse.builder()
                    .suggestedPrice(basePrice)
                    .basePrice(basePrice)
                    .deltaPercent(0.0)
                    .sport(sportType)
                    .fieldName("New Field")
                    .build();
        }

        return SuggestedPriceResponse.builder()
                .suggestedPrice(flaskResponse.getSuggestedPrice())
                .basePrice(basePrice)
                .deltaPercent(flaskResponse.getDeltaPercent())
                .sport(sportType)
                .fieldName("New Field")
                .build();
    }

}

package com.example.streetleague.schedule;

import com.example.streetleague.Entity.FieldReservation;
import com.example.streetleague.Entity.Tournament;
import com.example.streetleague.Repository.FieldReservationRepository;
import com.example.streetleague.Repository.TournamentRepository;
import com.example.streetleague.exception.ResourceNotFoundException;
import com.example.streetleague.Repository.FieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FieldScheduleServiceImp implements FieldScheduleService {

    private final FieldReservationRepository reservationRepository;
    private final TournamentRepository       tournamentRepository;
    private final FieldRepository            fieldRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FieldScheduleEntryDto> getFieldSchedule(Long fieldId, LocalDate from, LocalDate to) {

        // Vérifier que le terrain existe
        fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + fieldId));

        List<FieldScheduleEntryDto> entries = new ArrayList<>();

        // ── 1. Réservations ──────────────────────────────────────────────────
        LocalDateTime fromDt = from.atStartOfDay();          // 2026-04-01 00:00
        LocalDateTime toDt   = to.atTime(23, 59, 59);       // 2026-04-30 23:59

        List<FieldReservation> reservations =
                reservationRepository.findScheduleByFieldAndPeriod(fieldId, fromDt, toDt);

        for (FieldReservation r : reservations) {
            entries.add(FieldScheduleEntryDto.builder()
                    .type("RESERVATION")
                    .date(r.getStartTime().toLocalDate())
                    .startTime(r.getStartTime().toLocalTime())
                    .endTime(r.getEndTime().toLocalTime())
                    .sport(r.getField().getSportType().name())
                    .label(r.getPlayer().getFullName())
                    .status(r.getStatus().name())
                    .eventId(r.getId())
                    .tournamentType(null)
                    .build());
        }

        // ── 2. Tournois ──────────────────────────────────────────────────────
        List<Tournament> tournaments =
                tournamentRepository.findScheduleByFieldAndPeriod(fieldId, from, to);

        for (Tournament t : tournaments) {
            entries.add(FieldScheduleEntryDto.builder()
                    .type("TOURNAMENT")
                    .date(t.getStartDate())
                    .startTime(null)
                    .endTime(null)
                    .sport(t.getSportType().name())
                    .label(t.getName())
                    .status(t.getStatus().name())
                    .eventId(t.getId())
                    .tournamentType(t.getTournamentType().name())
                    .build());
        }

        // ── 3. Trier par date puis heure ─────────────────────────────────────
        entries.sort(Comparator
                .comparing(FieldScheduleEntryDto::getDate)
                .thenComparing(e -> e.getStartTime() != null ? e.getStartTime().toString() : ""));

        return entries;
    }
}
package com.example.streetleague.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldScheduleEntryDto {

    // "RESERVATION" ou "TOURNAMENT"
    private String type;

    // Date de l'événement
    private LocalDate date;

    // Heure début/fin — null pour les tournois
    private LocalTime startTime;
    private LocalTime endTime;

    // Sport pratiqué
    private String sport;

    // Nom du joueur (réservation) OU nom du tournoi
    private String label;

    // PENDING/APPROVED/REJECTED  →  réservation
    // UPCOMING/ONGOING/COMPLETED/CANCELLED  →  tournoi
    private String status;

    // Id de la réservation ou du tournoi
    private Long eventId;

    // INDIVIDUAL/TEAM — null si réservation
    private String tournamentType;
}
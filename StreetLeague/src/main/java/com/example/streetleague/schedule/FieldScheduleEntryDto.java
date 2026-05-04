package com.example.streetleague.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

//@Data
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(String tournamentType) {
        this.tournamentType = tournamentType;
    }

    public FieldScheduleEntryDto(String type, LocalDate date, LocalTime startTime, LocalTime endTime, String sport, String label, String status, Long eventId, String tournamentType) {
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sport = sport;
        this.label = label;
        this.status = status;
        this.eventId = eventId;
        this.tournamentType = tournamentType;
    }

    public FieldScheduleEntryDto() {

    }
}
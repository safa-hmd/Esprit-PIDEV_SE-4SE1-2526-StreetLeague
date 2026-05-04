package com.example.streetleague.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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

    public FieldScheduleEntryDto() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldScheduleEntryDto that = (FieldScheduleEntryDto) o;
        return Objects.equals(type, that.type) && Objects.equals(date, that.date) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, date, startTime, endTime, eventId);
    }

    @Override
    public String toString() {
        return "FieldScheduleEntryDto{" +
                "type='" + type + '\'' +
                ", date=" + date +
                ", label='" + label + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static FieldScheduleEntryDtoBuilder builder() {
        return new FieldScheduleEntryDtoBuilder();
    }

    public static class FieldScheduleEntryDtoBuilder {
        private String type;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private String sport;
        private String label;
        private String status;
        private Long eventId;
        private String tournamentType;

        public FieldScheduleEntryDtoBuilder type(String type) { this.type = type; return this; }
        public FieldScheduleEntryDtoBuilder date(LocalDate date) { this.date = date; return this; }
        public FieldScheduleEntryDtoBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public FieldScheduleEntryDtoBuilder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
        public FieldScheduleEntryDtoBuilder sport(String sport) { this.sport = sport; return this; }
        public FieldScheduleEntryDtoBuilder label(String label) { this.label = label; return this; }
        public FieldScheduleEntryDtoBuilder status(String status) { this.status = status; return this; }
        public FieldScheduleEntryDtoBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public FieldScheduleEntryDtoBuilder tournamentType(String tournamentType) { this.tournamentType = tournamentType; return this; }

        public FieldScheduleEntryDto build() {
            return new FieldScheduleEntryDto(type, date, startTime, endTime, sport, label, status, eventId, tournamentType);
        }
    }
}
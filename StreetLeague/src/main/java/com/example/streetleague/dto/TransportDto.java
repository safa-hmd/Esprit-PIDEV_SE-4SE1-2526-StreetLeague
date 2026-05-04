package com.example.streetleague.dto;

import com.example.streetleague.Entity.TransportType;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransportDto {
    private Long id;
    private TransportType type;
    private String destination;
    private Integer availableSeats;
    private Double pricePerSeat;
    private LocalDateTime departureTime;
    private LocalDateTime returnTime;
    private String status;
    private Long coachId;
    private String coachName;

    public TransportDto() {
    }

    public TransportDto(Long id, TransportType type, String destination, Integer availableSeats, Double pricePerSeat, LocalDateTime departureTime, LocalDateTime returnTime, String status, Long coachId, String coachName) {
        this.id = id;
        this.type = type;
        this.destination = destination;
        this.availableSeats = availableSeats;
        this.pricePerSeat = pricePerSeat;
        this.departureTime = departureTime;
        this.returnTime = returnTime;
        this.status = status;
        this.coachId = coachId;
        this.coachName = coachName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TransportType getType() { return type; }
    public void setType(TransportType type) { this.type = type; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public Double getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(Double pricePerSeat) { this.pricePerSeat = pricePerSeat; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getReturnTime() { return returnTime; }
    public void setReturnTime(LocalDateTime returnTime) { this.returnTime = returnTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportDto that = (TransportDto) o;
        return Objects.equals(id, that.id) && type == that.type && Objects.equals(destination, that.destination) && Objects.equals(availableSeats, that.availableSeats) && Objects.equals(pricePerSeat, that.pricePerSeat) && Objects.equals(departureTime, that.departureTime) && Objects.equals(returnTime, that.returnTime) && Objects.equals(status, that.status) && Objects.equals(coachId, that.coachId) && Objects.equals(coachName, that.coachName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, destination, availableSeats, pricePerSeat, departureTime, returnTime, status, coachId, coachName);
    }

    @Override
    public String toString() {
        return "TransportDto{" +
                "id=" + id +
                ", type=" + type +
                ", destination='" + destination + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static TransportDtoBuilder builder() {
        return new TransportDtoBuilder();
    }

    public static class TransportDtoBuilder {
        private Long id;
        private TransportType type;
        private String destination;
        private Integer availableSeats;
        private Double pricePerSeat;
        private LocalDateTime departureTime;
        private LocalDateTime returnTime;
        private String status;
        private Long coachId;
        private String coachName;

        public TransportDtoBuilder id(Long id) { this.id = id; return this; }
        public TransportDtoBuilder type(TransportType type) { this.type = type; return this; }
        public TransportDtoBuilder destination(String destination) { this.destination = destination; return this; }
        public TransportDtoBuilder availableSeats(Integer availableSeats) { this.availableSeats = availableSeats; return this; }
        public TransportDtoBuilder pricePerSeat(Double pricePerSeat) { this.pricePerSeat = pricePerSeat; return this; }
        public TransportDtoBuilder departureTime(LocalDateTime departureTime) { this.departureTime = departureTime; return this; }
        public TransportDtoBuilder returnTime(LocalDateTime returnTime) { this.returnTime = returnTime; return this; }
        public TransportDtoBuilder status(String status) { this.status = status; return this; }
        public TransportDtoBuilder coachId(Long coachId) { this.coachId = coachId; return this; }
        public TransportDtoBuilder coachName(String coachName) { this.coachName = coachName; return this; }

        public TransportDto build() {
            return new TransportDto(id, type, destination, availableSeats, pricePerSeat, departureTime, returnTime, status, coachId, coachName);
        }
    }
}
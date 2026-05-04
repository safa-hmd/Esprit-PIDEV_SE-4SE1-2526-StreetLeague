package com.example.streetleague.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransportType type;

    private Double pricePerSeat;
    @Column(nullable = true)
    private Integer availableSeats = 0;
    private LocalDateTime departureTime;
    private LocalDateTime returnTime;
    private String destination;

    @Column(nullable = false, length = 20)
    private String status = "APPROVED"; 

    @Column(name = "coach_id")
    private Long coachId;

    public Transport() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transport transport = (Transport) o;
        return Objects.equals(id, transport.id) && type == transport.type && Objects.equals(pricePerSeat, transport.pricePerSeat) && Objects.equals(availableSeats, transport.availableSeats) && Objects.equals(departureTime, transport.departureTime) && Objects.equals(returnTime, transport.returnTime) && Objects.equals(destination, transport.destination) && Objects.equals(status, transport.status) && Objects.equals(coachId, transport.coachId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, pricePerSeat, availableSeats, departureTime, returnTime, destination, status, coachId);
    }

    @Override
    public String toString() {
        return "Transport{" +
                "id=" + id +
                ", type=" + type +
                ", pricePerSeat=" + pricePerSeat +
                ", availableSeats=" + availableSeats +
                ", departureTime=" + departureTime +
                ", returnTime=" + returnTime +
                ", destination='" + destination + '\'' +
                ", status='" + status + '\'' +
                ", coachId=" + coachId +
                '}';
    }

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public TransportType getType() { return this.type; }
    public void setType(TransportType type) { this.type = type; }

    public Double getPricePerSeat() { return this.pricePerSeat; }
    public void setPricePerSeat(Double pricePerSeat) { this.pricePerSeat = pricePerSeat; }

    public Integer getAvailableSeats() { return this.availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public LocalDateTime getDepartureTime() { return this.departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getReturnTime() { return this.returnTime; }
    public void setReturnTime(LocalDateTime returnTime) { this.returnTime = returnTime; }

    public String getDestination() { return this.destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCoachId() { return this.coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    // ===== EXPLICIT CONSTRUCTOR ====
    public Transport(TransportType type, Double pricePerSeat, Integer availableSeats, 
                     LocalDateTime departureTime, LocalDateTime returnTime, String destination, String status, Long coachId) {
        this.type = type;
        this.pricePerSeat = pricePerSeat;
        this.availableSeats = availableSeats != null ? availableSeats : 0;
        this.departureTime = departureTime;
        this.returnTime = returnTime;
        this.destination = destination;
        this.status = status != null ? status : "APPROVED";
        this.coachId = coachId;
    }

    // ===== STATIC BUILDER METHOD ====
    public static TransportBuilder builder() {
        return new TransportBuilder();
    }

    public static class TransportBuilder {
        private TransportType type;
        private Double pricePerSeat;
        private Integer availableSeats;
        private LocalDateTime departureTime;
        private LocalDateTime returnTime;
        private String destination;
        private String status;
        private Long coachId;

        public TransportBuilder type(TransportType type) { this.type = type; return this; }
        public TransportBuilder pricePerSeat(Double pricePerSeat) { this.pricePerSeat = pricePerSeat; return this; }
        public TransportBuilder availableSeats(Integer availableSeats) { this.availableSeats = availableSeats; return this; }
        public TransportBuilder departureTime(LocalDateTime departureTime) { this.departureTime = departureTime; return this; }
        public TransportBuilder returnTime(LocalDateTime returnTime) { this.returnTime = returnTime; return this; }
        public TransportBuilder destination(String destination) { this.destination = destination; return this; }
        public TransportBuilder status(String status) { this.status = status; return this; }
        public TransportBuilder coachId(Long coachId) { this.coachId = coachId; return this; }

        public Transport build() {
            return new Transport(type, pricePerSeat, availableSeats, departureTime, returnTime, destination, status, coachId);
        }
    }
}

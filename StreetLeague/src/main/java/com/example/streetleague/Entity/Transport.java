package com.example.streetleague.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    TransportType type;

    Double pricePerSeat;
    @Column(nullable = true)
    private Integer availableSeats = 0;
    LocalDateTime departureTime;
    LocalDateTime returnTime;
    String destination;

    @Column(nullable = false)
    String status = "APPROVED"; 

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

    // ===== EXPLICIT CONSTRUCTOR ====
    public Transport(TransportType type, Double pricePerSeat, Integer availableSeats, 
                     LocalDateTime departureTime, LocalDateTime returnTime, String destination, String status) {
        this.type = type;
        this.pricePerSeat = pricePerSeat;
        this.availableSeats = availableSeats != null ? availableSeats : 0;
        this.departureTime = departureTime;
        this.returnTime = returnTime;
        this.destination = destination;
        this.status = status != null ? status : "APPROVED";
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

        public TransportBuilder type(TransportType type) { this.type = type; return this; }
        public TransportBuilder pricePerSeat(Double pricePerSeat) { this.pricePerSeat = pricePerSeat; return this; }
        public TransportBuilder availableSeats(Integer availableSeats) { this.availableSeats = availableSeats; return this; }
        public TransportBuilder departureTime(LocalDateTime departureTime) { this.departureTime = departureTime; return this; }
        public TransportBuilder returnTime(LocalDateTime returnTime) { this.returnTime = returnTime; return this; }
        public TransportBuilder destination(String destination) { this.destination = destination; return this; }
        public TransportBuilder status(String status) { this.status = status; return this; }

        public Transport build() {
            return new Transport(type, pricePerSeat, availableSeats, departureTime, returnTime, destination, status);
        }
    }
}

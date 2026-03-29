package com.example.streetleague.dto;

import com.example.streetleague.Entity.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportDto {
    private Long id;
    private TransportType type;        // ← enum pas String
    private String destination;
    private Integer availableSeats;
    private Double pricePerSeat;
    private LocalDateTime departureTime; // ← LocalDateTime pas String
    private LocalDateTime returnTime;    // ← LocalDateTime pas String

    // ===== EXPLICIT GETTERS/SETTERS =====
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public TransportType getType() { return this.type; }
    public void setType(TransportType type) { this.type = type; }

    public String getDestination() { return this.destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Integer getAvailableSeats() { return this.availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public Double getPricePerSeat() { return this.pricePerSeat; }
    public void setPricePerSeat(Double pricePerSeat) { this.pricePerSeat = pricePerSeat; }

    public LocalDateTime getDepartureTime() { return this.departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getReturnTime() { return this.returnTime; }
    public void setReturnTime(LocalDateTime returnTime) { this.returnTime = returnTime; }

    // ===== STATIC BUILDER HELPER =====
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

        public TransportDtoBuilder id(Long id) { this.id = id; return this; }
        public TransportDtoBuilder type(TransportType type) { this.type = type; return this; }
        public TransportDtoBuilder destination(String destination) { this.destination = destination; return this; }
        public TransportDtoBuilder availableSeats(Integer availableSeats) { this.availableSeats = availableSeats; return this; }
        public TransportDtoBuilder pricePerSeat(Double pricePerSeat) { this.pricePerSeat = pricePerSeat; return this; }
        public TransportDtoBuilder departureTime(LocalDateTime departureTime) { this.departureTime = departureTime; return this; }
        public TransportDtoBuilder returnTime(LocalDateTime returnTime) { this.returnTime = returnTime; return this; }

        public TransportDto build() {
            TransportDto dto = new TransportDto();
            dto.setId(id);
            dto.setType(type);
            dto.setDestination(destination);
            dto.setAvailableSeats(availableSeats);
            dto.setPricePerSeat(pricePerSeat);
            dto.setDepartureTime(departureTime);
            dto.setReturnTime(returnTime);
            return dto;
        }
    }
}
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
    private String status;
    private Long coachId;
    private String coachName;

    // ===== EXPLICIT GETTERS/SETTERS =====
    public String getCoachName() { return this.coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }
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

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCoachId() { return this.coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

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
            TransportDto dto = new TransportDto();
            dto.setId(id);
            dto.setType(type);
            dto.setDestination(destination);
            dto.setAvailableSeats(availableSeats);
            dto.setPricePerSeat(pricePerSeat);
            dto.setDepartureTime(departureTime);
            dto.setReturnTime(returnTime);
            dto.setStatus(status);
            dto.setCoachId(coachId);
            dto.setCoachName(coachName);
            return dto;
        }
    }
}
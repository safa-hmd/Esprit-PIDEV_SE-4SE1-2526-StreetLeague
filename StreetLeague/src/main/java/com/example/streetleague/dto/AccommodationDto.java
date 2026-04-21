package com.example.streetleague.dto;

import com.example.streetleague.Entity.AccommodationType;
import com.example.streetleague.Entity.AccommodationFormula;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDto {
    private Long id;
    private AccommodationType type;       // ← enum
    private AccommodationFormula formula; // ← enum
    private String address;
    private String status;
    private Integer capacity;
    private Integer numberOfNights;
    private Double pricePerNight;

    // ===== EXPLICIT GETTERS/SETTERS =====
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public AccommodationType getType() { return this.type; }
    public void setType(AccommodationType type) { this.type = type; }

    public AccommodationFormula getFormula() { return this.formula; }
    public void setFormula(AccommodationFormula formula) { this.formula = formula; }

    public String getAddress() { return this.address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCapacity() { return this.capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getNumberOfNights() { return this.numberOfNights; }
    public void setNumberOfNights(Integer numberOfNights) { this.numberOfNights = numberOfNights; }

    public Double getPricePerNight() { return this.pricePerNight; }
    public void setPricePerNight(Double pricePerNight) { this.pricePerNight = pricePerNight; }

    // ===== STATIC BUILDER HELPER =====
    public static AccommodationDtoBuilder builder() {
        return new AccommodationDtoBuilder();
    }

    public static class AccommodationDtoBuilder {
        private Long id;
        private AccommodationType type;
        private AccommodationFormula formula;
        private String address;
        private String status;
        private Integer capacity;
        private Integer numberOfNights;
        private Double pricePerNight;

        public AccommodationDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AccommodationDtoBuilder type(AccommodationType type) {
            this.type = type;
            return this;
        }

        public AccommodationDtoBuilder formula(AccommodationFormula formula) {
            this.formula = formula;
            return this;
        }

        public AccommodationDtoBuilder address(String address) {
            this.address = address;
            return this;
        }

        public AccommodationDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AccommodationDtoBuilder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public AccommodationDtoBuilder numberOfNights(Integer numberOfNights) {
            this.numberOfNights = numberOfNights;
            return this;
        }

        public AccommodationDtoBuilder pricePerNight(Double pricePerNight) {
            this.pricePerNight = pricePerNight;
            return this;
        }

        public AccommodationDto build() {
            AccommodationDto dto = new AccommodationDto();
            dto.setId(id);
            dto.setType(type);
            dto.setFormula(formula);
            dto.setAddress(address);
            dto.setStatus(status);
            dto.setCapacity(capacity);
            dto.setNumberOfNights(numberOfNights);
            dto.setPricePerNight(pricePerNight);
            return dto;
        }
    }
}
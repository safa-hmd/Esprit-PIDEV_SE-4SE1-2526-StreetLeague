package com.example.streetleague.dto;

import com.example.streetleague.Entity.AccommodationType;
import com.example.streetleague.Entity.AccommodationFormula;
import java.util.Objects;

public class AccommodationDto {
    private Long id;
    private AccommodationType type;       // ← enum
    private AccommodationFormula formula; // ← enum
    private String address;
    private String status;
    private Integer capacity;
    private Integer numberOfNights;
    private Double pricePerNight;

    public AccommodationDto() {
    }

    public AccommodationDto(Long id, AccommodationType type, AccommodationFormula formula, String address, String status, Integer capacity, Integer numberOfNights, Double pricePerNight) {
        this.id = id;
        this.type = type;
        this.formula = formula;
        this.address = address;
        this.status = status;
        this.capacity = capacity;
        this.numberOfNights = numberOfNights;
        this.pricePerNight = pricePerNight;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AccommodationType getType() { return type; }
    public void setType(AccommodationType type) { this.type = type; }

    public AccommodationFormula getFormula() { return formula; }
    public void setFormula(AccommodationFormula formula) { this.formula = formula; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(Integer numberOfNights) { this.numberOfNights = numberOfNights; }

    public Double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(Double pricePerNight) { this.pricePerNight = pricePerNight; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccommodationDto that = (AccommodationDto) o;
        return Objects.equals(id, that.id) && type == that.type && formula == that.formula && Objects.equals(address, that.address) && Objects.equals(status, that.status) && Objects.equals(capacity, that.capacity) && Objects.equals(numberOfNights, that.numberOfNights) && Objects.equals(pricePerNight, that.pricePerNight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, formula, address, status, capacity, numberOfNights, pricePerNight);
    }

    @Override
    public String toString() {
        return "AccommodationDto{" +
                "id=" + id +
                ", type=" + type +
                ", formula=" + formula +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                ", capacity=" + capacity +
                ", numberOfNights=" + numberOfNights +
                ", pricePerNight=" + pricePerNight +
                '}';
    }

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

        public AccommodationDtoBuilder id(Long id) { this.id = id; return this; }
        public AccommodationDtoBuilder type(AccommodationType type) { this.type = type; return this; }
        public AccommodationDtoBuilder formula(AccommodationFormula formula) { this.formula = formula; return this; }
        public AccommodationDtoBuilder address(String address) { this.address = address; return this; }
        public AccommodationDtoBuilder status(String status) { this.status = status; return this; }
        public AccommodationDtoBuilder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public AccommodationDtoBuilder numberOfNights(Integer numberOfNights) { this.numberOfNights = numberOfNights; return this; }
        public AccommodationDtoBuilder pricePerNight(Double pricePerNight) { this.pricePerNight = pricePerNight; return this; }

        public AccommodationDto build() {
            return new AccommodationDto(id, type, formula, address, status, capacity, numberOfNights, pricePerNight);
        }
    }
}
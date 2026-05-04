package com.example.streetleague.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccommodationType type;

    private Integer numberOfNights;
    private Double pricePerNight;

    @Column(name = "capacity", columnDefinition = "INT DEFAULT 0")
    private Integer capacity = 0;

    @Column(name = "address", nullable = true)
    private String address;

    @Enumerated(EnumType.STRING)
    private AccommodationFormula formula;

    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status = "PENDING";

    // OLD FIELDS — keep to avoid DB conflicts
    @Column(name = "arrival_date", nullable = true)
    private LocalDate arrivalDate;

    @Column(name = "departure_date", nullable = true)
    private LocalDate departureDate;

    @Column(name = "type_accommodation", nullable = true)
    private String typeAccommodation;

    @Column(name = "logistics_id", nullable = true)
    private Long logisticsId;

    public Accommodation() {
    }

    public Accommodation(Long id, AccommodationType type, Integer numberOfNights, Double pricePerNight, Integer capacity, String address, AccommodationFormula formula, String status, LocalDate arrivalDate, LocalDate departureDate, String typeAccommodation, Long logisticsId) {
        this.id = id;
        this.type = type;
        this.numberOfNights = numberOfNights;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.address = address;
        this.formula = formula;
        this.status = status;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.typeAccommodation = typeAccommodation;
        this.logisticsId = logisticsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accommodation that = (Accommodation) o;
        return Objects.equals(id, that.id) && type == that.type && Objects.equals(numberOfNights, that.numberOfNights) && Objects.equals(pricePerNight, that.pricePerNight) && Objects.equals(capacity, that.capacity) && Objects.equals(address, that.address) && formula == that.formula && Objects.equals(status, that.status) && Objects.equals(arrivalDate, that.arrivalDate) && Objects.equals(departureDate, that.departureDate) && Objects.equals(typeAccommodation, that.typeAccommodation) && Objects.equals(logisticsId, that.logisticsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, numberOfNights, pricePerNight, capacity, address, formula, status, arrivalDate, departureDate, typeAccommodation, logisticsId);
    }

    @Override
    public String toString() {
        return "Accommodation{" +
                "id=" + id +
                ", type=" + type +
                ", numberOfNights=" + numberOfNights +
                ", pricePerNight=" + pricePerNight +
                ", capacity=" + capacity +
                ", address='" + address + '\'' +
                ", formula=" + formula +
                ", status='" + status + '\'' +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                ", typeAccommodation='" + typeAccommodation + '\'' +
                ", logisticsId=" + logisticsId +
                '}';
    }

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public AccommodationType getType() { return this.type; }
    public void setType(AccommodationType type) { this.type = type; }

    public Integer getNumberOfNights() { return this.numberOfNights; }
    public void setNumberOfNights(Integer numberOfNights) { this.numberOfNights = numberOfNights; }

    public Double getPricePerNight() { return this.pricePerNight; }
    public void setPricePerNight(Double pricePerNight) { this.pricePerNight = pricePerNight; }

    public Integer getCapacity() { return this.capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getAddress() { return this.address; }
    public void setAddress(String address) { this.address = address; }

    public AccommodationFormula getFormula() { return this.formula; }
    public void setFormula(AccommodationFormula formula) { this.formula = formula; }

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getArrivalDate() { return this.arrivalDate; }
    public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

    public LocalDate getDepartureDate() { return this.departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public String getTypeAccommodation() { return this.typeAccommodation; }
    public void setTypeAccommodation(String typeAccommodation) { this.typeAccommodation = typeAccommodation; }

    public Long getLogisticsId() { return this.logisticsId; }
    public void setLogisticsId(Long logisticsId) { this.logisticsId = logisticsId; }

    // ===== STATIC BUILDER HELPER =====
    public static AccommodationBuilder builder() {
        return new AccommodationBuilder();
    }

    public static class AccommodationBuilder {
        private Long id;
        private AccommodationType type;
        private Integer numberOfNights;
        private Double pricePerNight;
        private Integer capacity = 0;
        private String address;
        private AccommodationFormula formula;
        private String status = "PENDING";
        private LocalDate arrivalDate;
        private LocalDate departureDate;
        private String typeAccommodation;
        private Long logisticsId;

        public AccommodationBuilder id(Long id) { this.id = id; return this; }
        public AccommodationBuilder type(AccommodationType type) { this.type = type; return this; }
        public AccommodationBuilder numberOfNights(Integer numberOfNights) { this.numberOfNights = numberOfNights; return this; }
        public AccommodationBuilder pricePerNight(Double pricePerNight) { this.pricePerNight = pricePerNight; return this; }
        public AccommodationBuilder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public AccommodationBuilder address(String address) { this.address = address; return this; }
        public AccommodationBuilder formula(AccommodationFormula formula) { this.formula = formula; return this; }
        public AccommodationBuilder status(String status) { this.status = status; return this; }
        public AccommodationBuilder arrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; return this; }
        public AccommodationBuilder departureDate(LocalDate departureDate) { this.departureDate = departureDate; return this; }
        public AccommodationBuilder typeAccommodation(String typeAccommodation) { this.typeAccommodation = typeAccommodation; return this; }
        public AccommodationBuilder logisticsId(Long logisticsId) { this.logisticsId = logisticsId; return this; }

        public Accommodation build() {
            return new Accommodation(id, type, numberOfNights, pricePerNight, capacity, address, formula, status, arrivalDate, departureDate, typeAccommodation, logisticsId);
        }
    }
}


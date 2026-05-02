package com.example.streetleague.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    AccommodationType type;

    Integer numberOfNights;
    Double pricePerNight;

    @Builder.Default
    @Column(name = "capacity", columnDefinition = "INT DEFAULT 0")
    private Integer capacity = 0;

    @Column(name = "address", nullable = true)
    private String address;

    @Enumerated(EnumType.STRING)
    AccommodationFormula formula;

    @Builder.Default
    @Column(name = "status",
            columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
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
            Accommodation acc = new Accommodation();
            acc.setId(id);
            acc.setType(type);
            acc.setNumberOfNights(numberOfNights);
            acc.setPricePerNight(pricePerNight);
            acc.setCapacity(capacity);
            acc.setAddress(address);
            acc.setFormula(formula);
            acc.setStatus(status);
            acc.setArrivalDate(arrivalDate);
            acc.setDepartureDate(departureDate);
            acc.setTypeAccommodation(typeAccommodation);
            acc.setLogisticsId(logisticsId);
            return acc;
        }
    }
}

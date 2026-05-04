package com.example.streetleague.Entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Column(nullable = false)
    private String location;

    private String imageUrl;

    private Double pricePerHour;

    private int capacity; // nombre de joueurs max

    @Column(nullable = false)
    private boolean available;

    // ---- Relations ----

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldReservation> reservations = new ArrayList<>();

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<FieldReservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<FieldReservation> reservations) {
        this.reservations = reservations;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Field(Long id, String name, String description, SportType sportType, String location, String imageUrl, Double pricePerHour, int capacity, boolean available, List<FieldReservation> reservations, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sportType = sportType;
        this.location = location;
        this.imageUrl = imageUrl;
        this.pricePerHour = pricePerHour;
        this.capacity = capacity;
        this.available = available;
        this.reservations = reservations;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Field() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return capacity == field.capacity && available == field.available && Objects.equals(id, field.id) && Objects.equals(name, field.name) && Objects.equals(description, field.description) && sportType == field.sportType && Objects.equals(location, field.location) && Objects.equals(imageUrl, field.imageUrl) && Objects.equals(pricePerHour, field.pricePerHour) && Objects.equals(latitude, field.latitude) && Objects.equals(longitude, field.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, sportType, location, imageUrl, pricePerHour, capacity, available, latitude, longitude);
    }

    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sportType=" + sportType +
                ", location='" + location + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", capacity=" + capacity +
                ", available=" + available +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public static FieldBuilder builder() {
        return new FieldBuilder();
    }

    public static class FieldBuilder {
        private Long id;
        private String name;
        private String description;
        private SportType sportType;
        private String location;
        private String imageUrl;
        private Double pricePerHour;
        private int capacity;
        private boolean available;
        private List<FieldReservation> reservations = new ArrayList<>();
        private Double latitude;
        private Double longitude;

        public FieldBuilder id(Long id) { this.id = id; return this; }
        public FieldBuilder name(String name) { this.name = name; return this; }
        public FieldBuilder description(String description) { this.description = description; return this; }
        public FieldBuilder sportType(SportType sportType) { this.sportType = sportType; return this; }
        public FieldBuilder location(String location) { this.location = location; return this; }
        public FieldBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public FieldBuilder pricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; return this; }
        public FieldBuilder capacity(int capacity) { this.capacity = capacity; return this; }
        public FieldBuilder available(boolean available) { this.available = available; return this; }
        public FieldBuilder reservations(List<FieldReservation> reservations) { this.reservations = reservations; return this; }
        public FieldBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public FieldBuilder longitude(Double longitude) { this.longitude = longitude; return this; }

        public Field build() {
            return new Field(id, name, description, sportType, location, imageUrl, pricePerHour, capacity, available, reservations, latitude, longitude);
        }
    }
}

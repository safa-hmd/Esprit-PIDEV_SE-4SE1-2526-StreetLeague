package com.example.streetleague.Entity;

import com.example.streetleague.Entity.FieldReservation;
import com.example.streetleague.Entity.SportType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fields")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
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
    @Builder.Default
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
}

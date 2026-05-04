package com.example.streetleague.dto;

import com.example.streetleague.Entity.SportType;
import jakarta.validation.constraints.*;
import java.util.Objects;

public class FieldDto {

    private Long id;

    @NotBlank(message = "Field name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;  // optionnel

    @NotNull(message = "Sport type is required")
    private SportType sportType;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;  // optionnel

    @NotNull(message = "Price per hour is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @DecimalMax(value = "10000.0", message = "Price cannot exceed 10,000")
    private Double pricePerHour;

    @Min(value = 1, message = "Capacity must be at least 1 player")
    @Max(value = 100, message = "Capacity cannot exceed 100 players")
    private int capacity;

    private boolean available;  // boolean, pas besoin de validation

    private Double latitude;
    private Double longitude;

    public FieldDto() {
    }

    public FieldDto(Long id, String name, String description, SportType sportType, String location, String imageUrl, Double pricePerHour, int capacity, boolean available, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sportType = sportType;
        this.location = location;
        this.imageUrl = imageUrl;
        this.pricePerHour = pricePerHour;
        this.capacity = capacity;
        this.available = available;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SportType getSportType() { return sportType; }
    public void setSportType(SportType sportType) { this.sportType = sportType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldDto fieldDto = (FieldDto) o;
        return capacity == fieldDto.capacity && available == fieldDto.available && Objects.equals(id, fieldDto.id) && Objects.equals(name, fieldDto.name) && Objects.equals(description, fieldDto.description) && sportType == fieldDto.sportType && Objects.equals(location, fieldDto.location) && Objects.equals(imageUrl, fieldDto.imageUrl) && Objects.equals(pricePerHour, fieldDto.pricePerHour) && Objects.equals(latitude, fieldDto.latitude) && Objects.equals(longitude, fieldDto.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, sportType, location, imageUrl, pricePerHour, capacity, available, latitude, longitude);
    }

    @Override
    public String toString() {
        return "FieldDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sportType=" + sportType +
                ", location='" + location + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", available=" + available +
                '}';
    }

    public static FieldDtoBuilder builder() {
        return new FieldDtoBuilder();
    }

    public static class FieldDtoBuilder {
        private Long id;
        private String name;
        private String description;
        private SportType sportType;
        private String location;
        private String imageUrl;
        private Double pricePerHour;
        private int capacity;
        private boolean available;
        private Double latitude;
        private Double longitude;

        public FieldDtoBuilder id(Long id) { this.id = id; return this; }
        public FieldDtoBuilder name(String name) { this.name = name; return this; }
        public FieldDtoBuilder description(String description) { this.description = description; return this; }
        public FieldDtoBuilder sportType(SportType sportType) { this.sportType = sportType; return this; }
        public FieldDtoBuilder location(String location) { this.location = location; return this; }
        public FieldDtoBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public FieldDtoBuilder pricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; return this; }
        public FieldDtoBuilder capacity(int capacity) { this.capacity = capacity; return this; }
        public FieldDtoBuilder available(boolean available) { this.available = available; return this; }
        public FieldDtoBuilder latitude(Double latitude) { this.latitude = latitude; return this; }
        public FieldDtoBuilder longitude(Double longitude) { this.longitude = longitude; return this; }

        public FieldDto build() {
            return new FieldDto(id, name, description, sportType, location, imageUrl, pricePerHour, capacity, available, latitude, longitude);
        }
    }
}
package com.example.streetleague.dto;

import com.example.streetleague.Entity.SportType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
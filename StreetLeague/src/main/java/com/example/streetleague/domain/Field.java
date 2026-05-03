package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Legacy POJO kept for backward compatibility.
 * The real JPA entity is com.example.streetleague.Entity.Field.
 * This class is NOT mapped as a JPA entity to avoid duplicate table mapping.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field {
    private Long id;
    private String name;
    private String location;
    private String type;
    private String description;
    private Double pricePerHour;
    private Integer capacity;
    private Boolean isAvailable = true;

    public boolean isAvailable() {
        return isAvailable != null && isAvailable;
    }

    public String getSportType() {
        return this.type != null ? this.type : "UNKNOWN";
    }

    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

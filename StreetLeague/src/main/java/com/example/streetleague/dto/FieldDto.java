package com.example.streetleague.dto;

import com.example.streetleague.Entity.SportType;
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
    private String name;
    private String description;
    private SportType sportType;
    private String location;
    private String imageUrl;
    private Double pricePerHour;
    private int capacity;
    private boolean available;
}

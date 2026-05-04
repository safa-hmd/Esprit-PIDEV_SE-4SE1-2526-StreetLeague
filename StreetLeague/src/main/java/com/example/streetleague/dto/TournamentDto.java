package com.example.streetleague.dto;

import com.example.streetleague.Entity.SportType;
import com.example.streetleague.Entity.TournamentStatus;
import com.example.streetleague.Entity.TournamentType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDto {
    private Long id;

    @NotBlank(message = "Tournament name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Sport type is required")
    private SportType sportType;

    @NotNull(message = "Tournament type is required")
    private TournamentType tournamentType;

    private TournamentStatus status;

    @NotNull(message = "Start date is required")
    //@FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Registration deadline is required")
    private LocalDate registrationDeadline;

    @Min(value = 2, message = "At least 2 participants are required")
    @Max(value = 256, message = "Cannot exceed 256 participants")
    private int maxParticipants;

    //@NotBlank(message = "Location is required")
    //@Size(max = 200, message = "Location cannot exceed 200 characters")
    //private String location;

    private Long fieldId;
    private String fieldName;      // pour affichage frontend (optionnel)
    private String fieldLocation;  // pour affichage frontend (optionnel)

    // city kept from LOGISTIQUE branch for compatibility
    private String city;

    @DecimalMin(value = "0.0", message = "Prize pool cannot be negative")
    private Double prizePool;

    private int registeredCount;

    // Explicit getters and setters from LOGISTIQUE branch
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return this.city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getStartDate() { return this.startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return this.endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
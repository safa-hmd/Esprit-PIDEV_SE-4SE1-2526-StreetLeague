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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentDto {

    private Long id;

    @NotBlank(message = "Tournament name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;  // optionnel

    @NotNull(message = "Sport type is required")
    private SportType sportType;

    @NotNull(message = "Tournament type is required")
    private TournamentType tournamentType;

    private TournamentStatus status;  // géré par le backend, pas besoin de validation

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Registration deadline is required")
    private LocalDate registrationDeadline;

    @Min(value = 2, message = "At least 2 participants are required")
    @Max(value = 256, message = "Cannot exceed 256 participants")
    private int maxParticipants;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @DecimalMin(value = "0.0", message = "Prize pool cannot be negative")
    private Double prizePool;  // optionnel

    private int registeredCount;
}
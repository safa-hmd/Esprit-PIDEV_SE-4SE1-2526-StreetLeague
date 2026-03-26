package com.example.streetleague.dto;

import com.example.streetleague.Entity.SportType;
import com.example.streetleague.Entity.TournamentStatus;
import com.example.streetleague.Entity.TournamentType;
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

    private String name;
    private String description;

    private SportType sportType;
    private TournamentType tournamentType;

    private TournamentStatus status;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;

    private int maxParticipants;

    private String location;

    private Double prizePool;

    private int registeredCount; // calculé à partir des registrations
}
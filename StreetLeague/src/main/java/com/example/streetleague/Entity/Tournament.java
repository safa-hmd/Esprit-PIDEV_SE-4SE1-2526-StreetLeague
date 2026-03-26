package com.example.streetleague.Entity;

import com.example.streetleague.Entity.SportType;
import com.example.streetleague.Entity.TournamentRegistration;
import com.example.streetleague.Entity.TournamentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentType tournamentType; // INDIVIDUAL ou TEAM

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate registrationDeadline;

    @Column(nullable = false)
    private int maxParticipants; // max joueurs ou max équipes

    private String location;

    private Double prizePool;

    // ---- Relations ----

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentRegistration> registrations = new ArrayList<>();
}
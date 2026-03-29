package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idTeam;

    String name;
    String sport;
    String description;
    LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    Level level;

    // The PLAYER who created the team becomes its captain
    @ManyToOne
    @JoinColumn(name = "captain_id", nullable = false)
    User captain;


    // Players belonging to this team
    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    List<User> players;

    // Training sessions for this team
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @JsonIgnore
    List<Training> trainings;

    // Matches where this team is Team A
    @OneToMany(mappedBy = "teamA")
    @JsonIgnore
    List<Match> matchesAsTeamA;

    // Matches where this team is Team B
    @OneToMany(mappedBy = "teamB")
    @JsonIgnore
    List<Match> matchesAsTeamB;

    // Inscriptions tournoi
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentRegistration> registrations = new ArrayList<>();
}
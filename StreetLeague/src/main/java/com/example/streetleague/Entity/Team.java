package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
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
    String city;

    int victories = 0;
    int defeats = 0;
    int matches = 0;

    @Builder.Default
    Integer eloScore = 1000;

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
    Set<User> players = new HashSet<>();

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


    @ManyToOne
    @JoinColumn(name = "coach_id")
    User coach;




    // ===== STATIC BUILDER METHOD ====
    public static TeamBuilder builder() {
        return new TeamBuilder();
    }

    public static class TeamBuilder {
        private String name;
        private String sport;
        private String description;
        private LocalDate creationDate;
        private String city;
        private Level level;
        private User captain;
        private List<User> players;
        private List<Training> trainings;
        private List<Match> matchesAsTeamA;
        private List<Match> matchesAsTeamB;

        public TeamBuilder name(String name) { this.name = name; return this; }
        public TeamBuilder sport(String sport) { this.sport = sport; return this; }
        public TeamBuilder description(String description) { this.description = description; return this; }
        public TeamBuilder creationDate(LocalDate creationDate) { this.creationDate = creationDate; return this; }
        public TeamBuilder city(String city) { this.city = city; return this; }
        public TeamBuilder level(Level level) { this.level = level; return this; }
        public TeamBuilder captain(User captain) { this.captain = captain; return this; }
        public TeamBuilder players(List<User> players) { this.players = players; return this; }
        public TeamBuilder trainings(List<Training> trainings) { this.trainings = trainings; return this; }
        public TeamBuilder matchesAsTeamA(List<Match> matchesAsTeamA) { this.matchesAsTeamA = matchesAsTeamA; return this; }
        public TeamBuilder matchesAsTeamB(List<Match> matchesAsTeamB) { this.matchesAsTeamB = matchesAsTeamB; return this; }

        public Team build() {
            Team team = new Team();
            team.name = this.name;
            team.sport = this.sport;
            team.description = this.description;
            team.creationDate = this.creationDate;
            team.city = this.city;
            team.level = this.level;
            team.captain = this.captain;

            team.trainings = this.trainings;
            team.matchesAsTeamA = this.matchesAsTeamA;
            team.matchesAsTeamB = this.matchesAsTeamB;
            return team;
        }
    }
}

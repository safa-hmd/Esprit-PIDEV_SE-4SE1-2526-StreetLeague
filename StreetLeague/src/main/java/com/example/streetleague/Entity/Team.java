package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

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

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====

    public Long getIdTeam() { return this.idTeam; }
    public void setIdTeam(Long idTeam) { this.idTeam = idTeam; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getSport() { return this.sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getCreationDate() { return this.creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public String getCity() { return this.city; }
    public void setCity(String city) { this.city = city; }

    public Level getLevel() { return this.level; }
    public void setLevel(Level level) { this.level = level; }

    public User getCaptain() { return this.captain; }
    public void setCaptain(User captain) { this.captain = captain; }

    public List<User> getPlayers() { return this.players; }
    public void setPlayers(List<User> players) { this.players = players; }

    public List<Training> getTrainings() { return this.trainings; }
    public void setTrainings(List<Training> trainings) { this.trainings = trainings; }

    public List<Match> getMatchesAsTeamA() { return this.matchesAsTeamA; }
    public void setMatchesAsTeamA(List<Match> matchesAsTeamA) { this.matchesAsTeamA = matchesAsTeamA; }

    public List<Match> getMatchesAsTeamB() { return this.matchesAsTeamB; }
    public void setMatchesAsTeamB(List<Match> matchesAsTeamB) { this.matchesAsTeamB = matchesAsTeamB; }

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
            team.players = this.players;
            team.trainings = this.trainings;
            team.matchesAsTeamA = this.matchesAsTeamA;
            team.matchesAsTeamB = this.matchesAsTeamB;
            return team;
        }
    }
}

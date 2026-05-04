package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTeam;

    private String name;
    private String sport;
    private String description;
    private LocalDate creationDate;
    private String city;

    private int victories = 0;
    private int defeats = 0;
    private int matches = 0;

    private Integer eloScore = 1000;

    @Enumerated(EnumType.STRING)
    private Level level;

    // The PLAYER who created the team becomes its captain
    @ManyToOne
    @JoinColumn(name = "captain_id", nullable = false)
    private User captain;


    // Players belonging to this team
    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> players = new HashSet<>();

    // Training sessions for this team
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Training> trainings;

    // Matches where this team is Team A
    @OneToMany(mappedBy = "teamA")
    @JsonIgnore
    private List<Match> matchesAsTeamA;

    // Matches where this team is Team B
    @OneToMany(mappedBy = "teamB")
    @JsonIgnore
    private List<Match> matchesAsTeamB;


    @ManyToOne
    @JoinColumn(name = "coach_id")
    private User coach;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return victories == team.victories && defeats == team.defeats && matches == team.matches && Objects.equals(idTeam, team.idTeam) && Objects.equals(name, team.name) && Objects.equals(sport, team.sport) && Objects.equals(description, team.description) && Objects.equals(creationDate, team.creationDate) && Objects.equals(city, team.city) && Objects.equals(eloScore, team.eloScore) && level == team.level && Objects.equals(captain, team.captain) && Objects.equals(players, team.players) && Objects.equals(trainings, team.trainings) && Objects.equals(matchesAsTeamA, team.matchesAsTeamA) && Objects.equals(matchesAsTeamB, team.matchesAsTeamB) && Objects.equals(coach, team.coach);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTeam, name, sport, description, creationDate, city, victories, defeats, matches, eloScore, level, captain, players, trainings, matchesAsTeamA, matchesAsTeamB, coach);
    }

    @Override
    public String toString() {
        return "Team{" +
                "idTeam=" + idTeam +
                ", name='" + name + '\'' +
                ", sport='" + sport + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", city='" + city + '\'' +
                ", victories=" + victories +
                ", defeats=" + defeats +
                ", matches=" + matches +
                ", eloScore=" + eloScore +
                ", level=" + level +
                ", captain=" + captain +
                ", coach=" + coach +
                '}';
    }

    // ===== STATIC BUILDER METHOD ====
    public static TeamBuilder builder() {
        return new TeamBuilder();
    }

    public static class TeamBuilder {
        private Long idTeam;
        private String name;
        private String sport;
        private String description;
        private LocalDate creationDate;
        private String city;
        private int victories;
        private int defeats;
        private int matches;
        private Integer eloScore = 1000;
        private Level level;
        private User captain;
        private Set<User> players = new HashSet<>();
        private List<Training> trainings;
        private List<Match> matchesAsTeamA;
        private List<Match> matchesAsTeamB;
        private User coach;

        public TeamBuilder idTeam(Long idTeam) { this.idTeam = idTeam; return this; }
        public TeamBuilder name(String name) { this.name = name; return this; }
        public TeamBuilder sport(String sport) { this.sport = sport; return this; }
        public TeamBuilder description(String description) { this.description = description; return this; }
        public TeamBuilder creationDate(LocalDate creationDate) { this.creationDate = creationDate; return this; }
        public TeamBuilder city(String city) { this.city = city; return this; }
        public TeamBuilder victories(int victories) { this.victories = victories; return this; }
        public TeamBuilder defeats(int defeats) { this.defeats = defeats; return this; }
        public TeamBuilder matches(int matches) { this.matches = matches; return this; }
        public TeamBuilder eloScore(Integer eloScore) { this.eloScore = eloScore; return this; }
        public TeamBuilder level(Level level) { this.level = level; return this; }
        public TeamBuilder captain(User captain) { this.captain = captain; return this; }
        public TeamBuilder players(Set<User> players) { this.players = players; return this; }
        public TeamBuilder trainings(List<Training> trainings) { this.trainings = trainings; return this; }
        public TeamBuilder matchesAsTeamA(List<Match> matchesAsTeamA) { this.matchesAsTeamA = matchesAsTeamA; return this; }
        public TeamBuilder matchesAsTeamB(List<Match> matchesAsTeamB) { this.matchesAsTeamB = matchesAsTeamB; return this; }
        public TeamBuilder coach(User coach) { this.coach = coach; return this; }

        public Team build() {
            return new Team(idTeam, name, sport, description, creationDate, city, victories, defeats, matches, eloScore, level, captain, players, trainings, matchesAsTeamA, matchesAsTeamB, coach);
        }
    }

    public Long getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(Long idTeam) {
        this.idTeam = idTeam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getVictories() {
        return victories;
    }

    public void setVictories(int victories) {
        this.victories = victories;
    }

    public int getDefeats() {
        return defeats;
    }

    public void setDefeats(int defeats) {
        this.defeats = defeats;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public Integer getEloScore() {
        return eloScore;
    }

    public void setEloScore(Integer eloScore) {
        this.eloScore = eloScore;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public User getCaptain() {
        return captain;
    }

    public void setCaptain(User captain) {
        this.captain = captain;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    public List<Match> getMatchesAsTeamA() {
        return matchesAsTeamA;
    }

    public void setMatchesAsTeamA(List<Match> matchesAsTeamA) {
        this.matchesAsTeamA = matchesAsTeamA;
    }

    public List<Match> getMatchesAsTeamB() {
        return matchesAsTeamB;
    }

    public void setMatchesAsTeamB(List<Match> matchesAsTeamB) {
        this.matchesAsTeamB = matchesAsTeamB;
    }

    public User getCoach() {
        return coach;
    }

    public void setCoach(User coach) {
        this.coach = coach;
    }

    public Team(Long idTeam, String name, String sport, String description, LocalDate creationDate, String city, int victories, int defeats, int matches, Integer eloScore, Level level, User captain, Set<User> players, List<Training> trainings, List<Match> matchesAsTeamA, List<Match> matchesAsTeamB, User coach) {
        this.idTeam = idTeam;
        this.name = name;
        this.sport = sport;
        this.description = description;
        this.creationDate = creationDate;
        this.city = city;
        this.victories = victories;
        this.defeats = defeats;
        this.matches = matches;
        this.eloScore = eloScore;
        this.level = level;
        this.captain = captain;
        this.players = players;
        this.trainings = trainings;
        this.matchesAsTeamA = matchesAsTeamA;
        this.matchesAsTeamB = matchesAsTeamB;
        this.coach = coach;
    }

    public Team() {

    }
}

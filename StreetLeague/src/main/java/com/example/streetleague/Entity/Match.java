package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "matchs")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMatch;

    private LocalDateTime matchDate;
    private String location;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private Integer scoreTeamA;
    private Integer scoreTeamB;
    private boolean statsUpdated = false;

    // Team A
    @ManyToOne
    @JoinColumn(name = "teamA_id", nullable = false)
    private Team teamA;

    // Team B
    @ManyToOne
    @JoinColumn(name = "teamB_id", nullable = false)
    private Team teamB;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = true)
    private User createdBy;

    public Match() {
    }

    public Match(Long idMatch, LocalDateTime matchDate, String location, MatchStatus status, Integer scoreTeamA, Integer scoreTeamB, boolean statsUpdated, Team teamA, Team teamB, User createdBy) {
        this.idMatch = idMatch;
        this.matchDate = matchDate;
        this.location = location;
        this.status = status;
        this.scoreTeamA = scoreTeamA;
        this.scoreTeamB = scoreTeamB;
        this.statsUpdated = statsUpdated;
        this.teamA = teamA;
        this.teamB = teamB;
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return statsUpdated == match.statsUpdated && Objects.equals(idMatch, match.idMatch) && Objects.equals(matchDate, match.matchDate) && Objects.equals(location, match.location) && status == match.status && Objects.equals(scoreTeamA, match.scoreTeamA) && Objects.equals(scoreTeamB, match.scoreTeamB) && Objects.equals(teamA, match.teamA) && Objects.equals(teamB, match.teamB) && Objects.equals(createdBy, match.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMatch, matchDate, location, status, scoreTeamA, scoreTeamB, statsUpdated, teamA, teamB, createdBy);
    }

    @Override
    public String toString() {
        return "Match{" +
                "idMatch=" + idMatch +
                ", matchDate=" + matchDate +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", scoreTeamA=" + scoreTeamA +
                ", scoreTeamB=" + scoreTeamB +
                ", statsUpdated=" + statsUpdated +
                ", teamA=" + teamA +
                ", teamB=" + teamB +
                ", createdBy=" + createdBy +
                '}';
    }

    public static MatchBuilder builder() {
        return new MatchBuilder();
    }

    public static class MatchBuilder {
        private Long idMatch;
        private LocalDateTime matchDate;
        private String location;
        private MatchStatus status;
        private Integer scoreTeamA;
        private Integer scoreTeamB;
        private boolean statsUpdated = false;
        private Team teamA;
        private Team teamB;
        private User createdBy;

        public MatchBuilder idMatch(Long idMatch) { this.idMatch = idMatch; return this; }
        public MatchBuilder matchDate(LocalDateTime matchDate) { this.matchDate = matchDate; return this; }
        public MatchBuilder location(String location) { this.location = location; return this; }
        public MatchBuilder status(MatchStatus status) { this.status = status; return this; }
        public MatchBuilder scoreTeamA(Integer scoreTeamA) { this.scoreTeamA = scoreTeamA; return this; }
        public MatchBuilder scoreTeamB(Integer scoreTeamB) { this.scoreTeamB = scoreTeamB; return this; }
        public MatchBuilder statsUpdated(boolean statsUpdated) { this.statsUpdated = statsUpdated; return this; }
        public MatchBuilder teamA(Team teamA) { this.teamA = teamA; return this; }
        public MatchBuilder teamB(Team teamB) { this.teamB = teamB; return this; }
        public MatchBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }

        public Match build() {
            return new Match(idMatch, matchDate, location, status, scoreTeamA, scoreTeamB, statsUpdated, teamA, teamB, createdBy);
        }
    }

    public boolean isStatsUpdated() { return this.statsUpdated; }
    public void setStatsUpdated(boolean statsUpdated) { this.statsUpdated = statsUpdated; }

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====

    public Long getIdMatch() { return this.idMatch; }
    public void setIdMatch(Long idMatch) { this.idMatch = idMatch; }

    public LocalDateTime getMatchDate() { return this.matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }

    public String getLocation() { return this.location; }
    public void setLocation(String location) { this.location = location; }

    public MatchStatus getStatus() { return this.status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public Integer getScoreTeamA() { return this.scoreTeamA; }
    public void setScoreTeamA(Integer scoreTeamA) { this.scoreTeamA = scoreTeamA; }

    public Integer getScoreTeamB() { return this.scoreTeamB; }
    public void setScoreTeamB(Integer scoreTeamB) { this.scoreTeamB = scoreTeamB; }

    public Team getTeamA() { return this.teamA; }
    public void setTeamA(Team teamA) { this.teamA = teamA; }

    public Team getTeamB() { return this.teamB; }
    public void setTeamB(Team teamB) { this.teamB = teamB; }

    public User getCreatedBy() { return this.createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
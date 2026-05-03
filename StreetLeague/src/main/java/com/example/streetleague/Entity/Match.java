package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "matchs")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idMatch;

    LocalDateTime matchDate;
    String location;

    @Enumerated(EnumType.STRING)
    MatchStatus status;

    Integer scoreTeamA;
    Integer scoreTeamB;

    private boolean statsUpdated = false;

    // Team A
    @ManyToOne
    @JoinColumn(name = "teamA_id", nullable = false)
    Team teamA;

    // Team B
    @ManyToOne
    @JoinColumn(name = "teamB_id", nullable = false)
    Team teamB;

    // The captain (PLAYER) who created/sent the match request
    /*@ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    User createdBy;*/

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = true)
    private User createdBy;

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
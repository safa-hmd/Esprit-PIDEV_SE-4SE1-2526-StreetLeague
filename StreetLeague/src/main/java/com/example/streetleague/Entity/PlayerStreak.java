package com.example.streetleague.Entity;

import jakarta.persistence.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "player_streaks")
public class PlayerStreak implements Persistable<Long> {

    @Id
    @Column(name = "player_id", nullable = false, unique = true)
    private Long playerId;

    @Transient
    private boolean isNew = true;

    private Integer currentStreak = 0;
    private Integer bestStreak = 0;
    private Integer totalPoints = 0;
    private Double momentumScore = 0.0;
    private Double fatigueRisk = 0.0;

    private String currentBadge;
    private LocalDate lastAttendanceDate;
    private LocalDateTime lastUpdated;
    private LocalDateTime synergyBonusAwardedAt;

    public PlayerStreak() {
    }

    public PlayerStreak(Long playerId, boolean isNew, Integer currentStreak, Integer bestStreak, Integer totalPoints, Double momentumScore, Double fatigueRisk, String currentBadge, LocalDate lastAttendanceDate, LocalDateTime lastUpdated, LocalDateTime synergyBonusAwardedAt) {
        this.playerId = playerId;
        this.isNew = isNew;
        this.currentStreak = currentStreak != null ? currentStreak : 0;
        this.bestStreak = bestStreak != null ? bestStreak : 0;
        this.totalPoints = totalPoints != null ? totalPoints : 0;
        this.momentumScore = momentumScore != null ? momentumScore : 0.0;
        this.fatigueRisk = fatigueRisk != null ? fatigueRisk : 0.0;
        this.currentBadge = currentBadge;
        this.lastAttendanceDate = lastAttendanceDate;
        this.lastUpdated = lastUpdated;
        this.synergyBonusAwardedAt = synergyBonusAwardedAt;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(Integer bestStreak) {
        this.bestStreak = bestStreak;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Double getMomentumScore() {
        return momentumScore;
    }

    public void setMomentumScore(Double momentumScore) {
        this.momentumScore = momentumScore;
    }

    public Double getFatigueRisk() {
        return fatigueRisk;
    }

    public void setFatigueRisk(Double fatigueRisk) {
        this.fatigueRisk = fatigueRisk;
    }

    public String getCurrentBadge() {
        return currentBadge;
    }

    public void setCurrentBadge(String currentBadge) {
        this.currentBadge = currentBadge;
    }

    public LocalDate getLastAttendanceDate() {
        return lastAttendanceDate;
    }

    public void setLastAttendanceDate(LocalDate lastAttendanceDate) {
        this.lastAttendanceDate = lastAttendanceDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getSynergyBonusAwardedAt() {
        return synergyBonusAwardedAt;
    }

    public void setSynergyBonusAwardedAt(LocalDateTime synergyBonusAwardedAt) {
        this.synergyBonusAwardedAt = synergyBonusAwardedAt;
    }

    @Override
    public Long getId() { return playerId; }

    @Override
    public boolean isNew() { return isNew; }

    @PostPersist
    @PostLoad
    void markNotNew() { this.isNew = false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerStreak that = (PlayerStreak) o;
        return isNew == that.isNew && Objects.equals(playerId, that.playerId) && Objects.equals(currentStreak, that.currentStreak) && Objects.equals(bestStreak, that.bestStreak) && Objects.equals(totalPoints, that.totalPoints) && Objects.equals(momentumScore, that.momentumScore) && Objects.equals(fatigueRisk, that.fatigueRisk) && Objects.equals(currentBadge, that.currentBadge) && Objects.equals(lastAttendanceDate, that.lastAttendanceDate) && Objects.equals(lastUpdated, that.lastUpdated) && Objects.equals(synergyBonusAwardedAt, that.synergyBonusAwardedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, isNew, currentStreak, bestStreak, totalPoints, momentumScore, fatigueRisk, currentBadge, lastAttendanceDate, lastUpdated, synergyBonusAwardedAt);
    }

    @Override
    public String toString() {
        return "PlayerStreak{" +
                "playerId=" + playerId +
                ", isNew=" + isNew +
                ", currentStreak=" + currentStreak +
                ", bestStreak=" + bestStreak +
                ", totalPoints=" + totalPoints +
                ", momentumScore=" + momentumScore +
                ", fatigueRisk=" + fatigueRisk +
                ", currentBadge='" + currentBadge + '\'' +
                ", lastAttendanceDate=" + lastAttendanceDate +
                ", lastUpdated=" + lastUpdated +
                ", synergyBonusAwardedAt=" + synergyBonusAwardedAt +
                '}';
    }

    public static PlayerStreakBuilder builder() {
        return new PlayerStreakBuilder();
    }

    public PlayerStreakBuilder toBuilder() {
        return new PlayerStreakBuilder()
                .playerId(this.playerId)
                .isNew(this.isNew)
                .currentStreak(this.currentStreak)
                .bestStreak(this.bestStreak)
                .totalPoints(this.totalPoints)
                .momentumScore(this.momentumScore)
                .fatigueRisk(this.fatigueRisk)
                .currentBadge(this.currentBadge)
                .lastAttendanceDate(this.lastAttendanceDate)
                .lastUpdated(this.lastUpdated)
                .synergyBonusAwardedAt(this.synergyBonusAwardedAt);
    }

    public static class PlayerStreakBuilder {
        private Long playerId;
        private boolean isNew = true;
        private Integer currentStreak = 0;
        private Integer bestStreak = 0;
        private Integer totalPoints = 0;
        private Double momentumScore = 0.0;
        private Double fatigueRisk = 0.0;
        private String currentBadge;
        private LocalDate lastAttendanceDate;
        private LocalDateTime lastUpdated;
        private LocalDateTime synergyBonusAwardedAt;

        public PlayerStreakBuilder playerId(Long playerId) { this.playerId = playerId; return this; }
        public PlayerStreakBuilder isNew(boolean isNew) { this.isNew = isNew; return this; }
        public PlayerStreakBuilder currentStreak(Integer currentStreak) { this.currentStreak = currentStreak; return this; }
        public PlayerStreakBuilder bestStreak(Integer bestStreak) { this.bestStreak = bestStreak; return this; }
        public PlayerStreakBuilder totalPoints(Integer totalPoints) { this.totalPoints = totalPoints; return this; }
        public PlayerStreakBuilder momentumScore(Double momentumScore) { this.momentumScore = momentumScore; return this; }
        public PlayerStreakBuilder fatigueRisk(Double fatigueRisk) { this.fatigueRisk = fatigueRisk; return this; }
        public PlayerStreakBuilder currentBadge(String currentBadge) { this.currentBadge = currentBadge; return this; }
        public PlayerStreakBuilder lastAttendanceDate(LocalDate lastAttendanceDate) { this.lastAttendanceDate = lastAttendanceDate; return this; }
        public PlayerStreakBuilder lastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; return this; }
        public PlayerStreakBuilder synergyBonusAwardedAt(LocalDateTime synergyBonusAwardedAt) { this.synergyBonusAwardedAt = synergyBonusAwardedAt; return this; }

        public PlayerStreak build() {
            return new PlayerStreak(playerId, isNew, currentStreak, bestStreak, totalPoints, momentumScore, fatigueRisk, currentBadge, lastAttendanceDate, lastUpdated, synergyBonusAwardedAt);
        }
    }
}
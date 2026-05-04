package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class WaterStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private int currentStreak;     // jours consécutifs actuels
    private int longestStreak;     // record personnel
    private LocalDate lastGoalDate; // dernier jour où le goal a été atteint

    public WaterStreak() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public LocalDate getLastGoalDate() {
        return lastGoalDate;
    }

    public void setLastGoalDate(LocalDate lastGoalDate) {
        this.lastGoalDate = lastGoalDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterStreak that = (WaterStreak) o;
        return currentStreak == that.currentStreak && longestStreak == that.longestStreak && Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(lastGoalDate, that.lastGoalDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, currentStreak, longestStreak, lastGoalDate);
    }

    @Override
    public String toString() {
        return "WaterStreak{" +
                "id=" + id +
                ", user=" + user +
                ", currentStreak=" + currentStreak +
                ", longestStreak=" + longestStreak +
                ", lastGoalDate=" + lastGoalDate +
                '}';
    }
}
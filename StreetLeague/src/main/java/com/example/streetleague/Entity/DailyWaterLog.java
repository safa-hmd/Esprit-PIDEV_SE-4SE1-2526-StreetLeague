package com.example.streetleague.Entity;


import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class DailyWaterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalMl;         // total bu dans la journée
    private int goalMl;          // goal fixé par l'user
    private boolean goalReached; // objectif atteint ?
    private LocalDate date;

    @ManyToOne
    private User user;

    public DailyWaterLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTotalMl() {
        return totalMl;
    }

    public void setTotalMl(int totalMl) {
        this.totalMl = totalMl;
    }

    public int getGoalMl() {
        return goalMl;
    }

    public void setGoalMl(int goalMl) {
        this.goalMl = goalMl;
    }

    public boolean isGoalReached() {
        return goalReached;
    }

    public void setGoalReached(boolean goalReached) {
        this.goalReached = goalReached;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyWaterLog that = (DailyWaterLog) o;
        return totalMl == that.totalMl && goalMl == that.goalMl && goalReached == that.goalReached && Objects.equals(id, that.id) && Objects.equals(date, that.date) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, totalMl, goalMl, goalReached, date, user);
    }

    @Override
    public String toString() {
        return "DailyWaterLog{" +
                "id=" + id +
                ", totalMl=" + totalMl +
                ", goalMl=" + goalMl +
                ", goalReached=" + goalReached +
                ", date=" + date +
                ", user=" + user +
                '}';
    }
}


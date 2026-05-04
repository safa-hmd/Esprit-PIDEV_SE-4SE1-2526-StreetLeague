package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class UserGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String goalType;    // "WATER" ou "BMI"
    private double targetValue; // ex: 2000 ml ou BMI 22.0
    private boolean achieved;

    @ManyToOne
    private User user;

    public UserGoal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
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
        UserGoal userGoal = (UserGoal) o;
        return Double.compare(targetValue, userGoal.targetValue) == 0 && achieved == userGoal.achieved && Objects.equals(id, userGoal.id) && Objects.equals(goalType, userGoal.goalType) && Objects.equals(user, userGoal.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, goalType, targetValue, achieved, user);
    }

    @Override
    public String toString() {
        return "UserGoal{" +
                "id=" + id +
                ", goalType='" + goalType + '\'' +
                ", targetValue=" + targetValue +
                ", achieved=" + achieved +
                ", user=" + user +
                '}';
    }
}


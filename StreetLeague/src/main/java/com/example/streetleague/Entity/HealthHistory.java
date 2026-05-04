package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class HealthHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double weight;
    private double height;
    private double bmi;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public HealthHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
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
        HealthHistory that = (HealthHistory) o;
        return Double.compare(weight, that.weight) == 0 && Double.compare(height, that.height) == 0 && Double.compare(bmi, that.bmi) == 0 && Objects.equals(id, that.id) && Objects.equals(date, that.date) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight, height, bmi, date, user);
    }

    @Override
    public String toString() {
        return "HealthHistory{" +
                "id=" + id +
                ", weight=" + weight +
                ", height=" + height +
                ", bmi=" + bmi +
                ", date=" + date +
                ", user=" + user +
                '}';
    }
}
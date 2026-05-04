package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String badgeType;
    private String description;
    private LocalDate earnedDate;

    @ManyToOne
    private User user;

    public UserBadge() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEarnedDate() {
        return earnedDate;
    }

    public void setEarnedDate(LocalDate earnedDate) {
        this.earnedDate = earnedDate;
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
        UserBadge userBadge = (UserBadge) o;
        return Objects.equals(id, userBadge.id) && Objects.equals(badgeType, userBadge.badgeType) && Objects.equals(description, userBadge.description) && Objects.equals(earnedDate, userBadge.earnedDate) && Objects.equals(user, userBadge.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, badgeType, description, earnedDate, user);
    }

    @Override
    public String toString() {
        return "UserBadge{" +
                "id=" + id +
                ", badgeType='" + badgeType + '\'' +
                ", description='" + description + '\'' +
                ", earnedDate=" + earnedDate +
                ", user=" + user +
                '}';
    }
}


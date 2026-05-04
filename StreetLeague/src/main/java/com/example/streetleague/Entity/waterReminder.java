package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class waterReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;
    private int frequency;
    private int quantity;
    private boolean active;

    @ManyToOne
    @JsonIgnore
    private User user;

    public waterReminder() {
    }

    public waterReminder(Long id, LocalDateTime time, int frequency, int quantity, boolean active, User user) {
        this.id = id;
        this.time = time;
        this.frequency = frequency;
        this.quantity = quantity;
        this.active = active;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
        waterReminder that = (waterReminder) o;
        return frequency == that.frequency && quantity == that.quantity && active == that.active && Objects.equals(id, that.id) && Objects.equals(time, that.time) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, frequency, quantity, active, user);
    }

    @Override
    public String toString() {
        return "waterReminder{" +
                "id=" + id +
                ", time=" + time +
                ", frequency=" + frequency +
                ", quantity=" + quantity +
                ", active=" + active +
                ", user=" + user +
                '}';
    }

    public static waterReminderBuilder builder() {
        return new waterReminderBuilder();
    }

    public static class waterReminderBuilder {
        private Long id;
        private LocalDateTime time;
        private int frequency;
        private int quantity;
        private boolean active;
        private User user;

        public waterReminderBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public waterReminderBuilder time(LocalDateTime time) {
            this.time = time;
            return this;
        }

        public waterReminderBuilder frequency(int frequency) {
            this.frequency = frequency;
            return this;
        }

        public waterReminderBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public waterReminderBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public waterReminderBuilder user(User user) {
            this.user = user;
            return this;
        }

        public waterReminder build() {
            return new waterReminder(id, time, frequency, quantity, active, user);
        }
    }
}


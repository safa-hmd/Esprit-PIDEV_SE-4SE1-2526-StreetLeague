package com.example.streetleague.Entity;


import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idNotification;

    @Column(nullable = false, length = 1000)  
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Long getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(Long idNotification) {
        this.idNotification = idNotification;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notification() {
    }

    public Notification(Long idNotification, String message, boolean isRead, LocalDateTime createdAt, User user) {
        this.idNotification = idNotification;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return isRead == that.isRead && Objects.equals(idNotification, that.idNotification) && Objects.equals(message, that.message) && Objects.equals(createdAt, that.createdAt) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNotification, message, isRead, createdAt, user);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "idNotification=" + idNotification +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                ", user=" + user +
                '}';
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private Long idNotification;
        private String message;
        private boolean isRead = false;
        private LocalDateTime createdAt = LocalDateTime.now();
        private User user;

        public NotificationBuilder idNotification(Long idNotification) { this.idNotification = idNotification; return this; }
        public NotificationBuilder message(String message) { this.message = message; return this; }
        public NotificationBuilder isRead(boolean isRead) { this.isRead = isRead; return this; }
        public NotificationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public NotificationBuilder user(User user) { this.user = user; return this; }

        public Notification build() {
            return new Notification(idNotification, message, isRead, createdAt, user);
        }
    }
}



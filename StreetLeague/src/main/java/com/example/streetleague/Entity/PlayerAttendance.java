package com.example.streetleague.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "player_attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "attendance_date"}))
public class PlayerAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    private Boolean isPresent = false;

    private Integer intensity = 1;     // 1–5

    private String attendanceType;     // TRAINING | MATCH | BOTH

    public PlayerAttendance() {
    }

    public PlayerAttendance(Long id, Long playerId, LocalDate attendanceDate, Boolean isPresent, Integer intensity, String attendanceType) {
        this.id = id;
        this.playerId = playerId;
        this.attendanceDate = attendanceDate;
        this.isPresent = isPresent != null ? isPresent : false;
        this.intensity = intensity != null ? intensity : 1;
        this.attendanceType = attendanceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Boolean getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Boolean isPresent) {
        this.isPresent = isPresent;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerAttendance that = (PlayerAttendance) o;
        return Objects.equals(id, that.id) && Objects.equals(playerId, that.playerId) && Objects.equals(attendanceDate, that.attendanceDate) && Objects.equals(isPresent, that.isPresent) && Objects.equals(intensity, that.intensity) && Objects.equals(attendanceType, that.attendanceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playerId, attendanceDate, isPresent, intensity, attendanceType);
    }

    @Override
    public String toString() {
        return "PlayerAttendance{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", attendanceDate=" + attendanceDate +
                ", isPresent=" + isPresent +
                ", intensity=" + intensity +
                ", attendanceType='" + attendanceType + '\'' +
                '}';
    }

    public static PlayerAttendanceBuilder builder() {
        return new PlayerAttendanceBuilder();
    }

    public static class PlayerAttendanceBuilder {
        private Long id;
        private Long playerId;
        private LocalDate attendanceDate;
        private Boolean isPresent = false;
        private Integer intensity = 1;
        private String attendanceType;

        public PlayerAttendanceBuilder id(Long id) { this.id = id; return this; }
        public PlayerAttendanceBuilder playerId(Long playerId) { this.playerId = playerId; return this; }
        public PlayerAttendanceBuilder attendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; return this; }
        public PlayerAttendanceBuilder isPresent(Boolean isPresent) { this.isPresent = isPresent; return this; }
        public PlayerAttendanceBuilder intensity(Integer intensity) { this.intensity = intensity; return this; }
        public PlayerAttendanceBuilder attendanceType(String attendanceType) { this.attendanceType = attendanceType; return this; }

        public PlayerAttendance build() {
            return new PlayerAttendance(id, playerId, attendanceDate, isPresent, intensity, attendanceType);
        }
    }

    // NOTE : teamId a été supprimé volontairement (architecture simplifiée sans équipe).
    // La contrainte UNIQUE sur (player_id, attendance_date) empêche un double check-in
    // pour le même joueur le même jour — c'est le comportement voulu.
}
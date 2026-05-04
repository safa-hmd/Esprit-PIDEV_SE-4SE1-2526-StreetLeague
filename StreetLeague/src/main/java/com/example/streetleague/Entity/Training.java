package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTraining;

    private String title;
    private String description;

    private LocalDateTime trainingDate;
    private Integer durationInMinutes;
    private String location;
    private String exercises;
    private String performanceReport;

    @Enumerated(EnumType.STRING)
    private TrainingStatus status;

    // Team this training belongs to
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // Coach who created and manages this training
    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = true)
    private User coach;

    // Players who joined this training session
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "training_participants",
            joinColumns = @JoinColumn(name = "trainings_id_training"),
            inverseJoinColumns = @JoinColumn(name = "participants_id_user")  // ← nom exact de la colonne User PK
    )
    private List<User> participants = new ArrayList<>();

    public Training() {
    }

    public Training(Long idTraining, String title, String description, LocalDateTime trainingDate, Integer durationInMinutes, String location, String exercises, String performanceReport, TrainingStatus status, Team team, User coach, List<User> participants) {
        this.idTraining = idTraining;
        this.title = title;
        this.description = description;
        this.trainingDate = trainingDate;
        this.durationInMinutes = durationInMinutes;
        this.location = location;
        this.exercises = exercises;
        this.performanceReport = performanceReport;
        this.status = status;
        this.team = team;
        this.coach = coach;
        this.participants = participants != null ? participants : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return Objects.equals(idTraining, training.idTraining) && Objects.equals(title, training.title) && Objects.equals(description, training.description) && Objects.equals(trainingDate, training.trainingDate) && Objects.equals(durationInMinutes, training.durationInMinutes) && Objects.equals(location, training.location) && Objects.equals(exercises, training.exercises) && Objects.equals(performanceReport, training.performanceReport) && status == training.status && Objects.equals(team, training.team) && Objects.equals(coach, training.coach) && Objects.equals(participants, training.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTraining, title, description, trainingDate, durationInMinutes, location, exercises, performanceReport, status, team, coach, participants);
    }

    @Override
    public String toString() {
        return "Training{" +
                "idTraining=" + idTraining +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", trainingDate=" + trainingDate +
                ", durationInMinutes=" + durationInMinutes +
                ", location='" + location + '\'' +
                ", exercises='" + exercises + '\'' +
                ", performanceReport='" + performanceReport + '\'' +
                ", status=" + status +
                ", team=" + team +
                ", coach=" + coach +
                ", participants=" + participants +
                '}';
    }

    public static TrainingBuilder builder() {
        return new TrainingBuilder();
    }

    public static class TrainingBuilder {
        private Long idTraining;
        private String title;
        private String description;
        private LocalDateTime trainingDate;
        private Integer durationInMinutes;
        private String location;
        private String exercises;
        private String performanceReport;
        private TrainingStatus status;
        private Team team;
        private User coach;
        private List<User> participants = new ArrayList<>();

        public TrainingBuilder idTraining(Long idTraining) { this.idTraining = idTraining; return this; }
        public TrainingBuilder title(String title) { this.title = title; return this; }
        public TrainingBuilder description(String description) { this.description = description; return this; }
        public TrainingBuilder trainingDate(LocalDateTime trainingDate) { this.trainingDate = trainingDate; return this; }
        public TrainingBuilder durationInMinutes(Integer durationInMinutes) { this.durationInMinutes = durationInMinutes; return this; }
        public TrainingBuilder location(String location) { this.location = location; return this; }
        public TrainingBuilder exercises(String exercises) { this.exercises = exercises; return this; }
        public TrainingBuilder performanceReport(String performanceReport) { this.performanceReport = performanceReport; return this; }
        public TrainingBuilder status(TrainingStatus status) { this.status = status; return this; }
        public TrainingBuilder team(Team team) { this.team = team; return this; }
        public TrainingBuilder coach(User coach) { this.coach = coach; return this; }
        public TrainingBuilder participants(List<User> participants) { this.participants = participants; return this; }

        public Training build() {
            return new Training(idTraining, title, description, trainingDate, durationInMinutes, location, exercises, performanceReport, status, team, coach, participants);
        }
    }

    // ===== EXPLICIT GETTERS/SETTERS (Lombok not processing) =====

    public Long getIdTraining() { return this.idTraining; }
    public void setIdTraining(Long idTraining) { this.idTraining = idTraining; }

    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTrainingDate() { return this.trainingDate; }
    public void setTrainingDate(LocalDateTime trainingDate) { this.trainingDate = trainingDate; }

    public Integer getDurationInMinutes() { return this.durationInMinutes; }
    public void setDurationInMinutes(Integer durationInMinutes) { this.durationInMinutes = durationInMinutes; }

    public String getLocation() { return this.location; }
    public void setLocation(String location) { this.location = location; }

    public String getExercises() { return this.exercises; }
    public void setExercises(String exercises) { this.exercises = exercises; }

    public String getPerformanceReport() { return this.performanceReport; }
    public void setPerformanceReport(String performanceReport) { this.performanceReport = performanceReport; }

    public TrainingStatus getStatus() { return this.status; }
    public void setStatus(TrainingStatus status) { this.status = status; }

    public Team getTeam() { return this.team; }
    public void setTeam(Team team) { this.team = team; }

    public User getCoach() { return this.coach; }
    public void setCoach(User coach) { this.coach = coach; }

    public List<User> getParticipants() { return this.participants; }
    public void setParticipants(List<User> participants) { this.participants = participants; }
}
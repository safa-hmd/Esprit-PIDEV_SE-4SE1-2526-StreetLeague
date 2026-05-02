package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idTraining;

    String title;
    String description;

    LocalDateTime trainingDate;
    Integer durationInMinutes;
    String location;
    String exercises;
    String performanceReport;

    @Enumerated(EnumType.STRING)
    TrainingStatus status;

    // Team this training belongs to
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    Team team;

    // Coach who created and manages this training
    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = true)
    User coach;

    // Players who joined this training session
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "training_participants",
            joinColumns = @JoinColumn(name = "trainings_id_training"),
            inverseJoinColumns = @JoinColumn(name = "participants_id_user")  // ← nom exact de la colonne User PK
    )
    private List<User> participants = new ArrayList<>();

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
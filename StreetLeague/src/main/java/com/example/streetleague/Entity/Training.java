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
@Getter
@Setter
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
    @JoinTable(
            name = "training_participants",
            joinColumns = @JoinColumn(name = "trainings_id_training"),
            inverseJoinColumns = @JoinColumn(name = "participants_id_user")  // ← nom exact de la colonne User PK
    )
    private List<User> participants = new ArrayList<>();

}
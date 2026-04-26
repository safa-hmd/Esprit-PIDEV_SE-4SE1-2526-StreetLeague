package com.example.streetleague.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "player_attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "attendance_date"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PlayerAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Builder.Default
    private Boolean isPresent = false;

    @Builder.Default
    private Integer intensity = 1;     // 1–5

    private String attendanceType;     // TRAINING | MATCH | BOTH

    // NOTE : teamId a été supprimé volontairement (architecture simplifiée sans équipe).
    // La contrainte UNIQUE sur (player_id, attendance_date) empêche un double check-in
    // pour le même joueur le même jour — c'est le comportement voulu.
}
package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class DailyWaterLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalMl;         // total bu dans la journée
    private int goalMl;          // goal fixé par l'user
    private boolean goalReached; // objectif atteint ?
    private LocalDate date;

    @ManyToOne
    private User user;
}

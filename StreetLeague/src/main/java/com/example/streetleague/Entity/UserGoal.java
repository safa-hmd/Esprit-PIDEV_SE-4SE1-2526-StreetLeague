package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UserGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String goalType;    // "WATER" ou "BMI"
    private double targetValue; // ex: 2000 ml ou BMI 22.0
    private boolean achieved;

    @ManyToOne
    private User user;
}

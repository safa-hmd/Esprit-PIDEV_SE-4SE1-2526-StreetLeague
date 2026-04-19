package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String badgeType;   // "WATER_WEEK", "BMI_GOAL", "FREE_DELIVERY", "COUPON_5", "COUPON_10"
    private String description;
    private LocalDate earnedDate;

    @ManyToOne
    private User user;
}

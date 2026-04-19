package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String goal;
    private String status;
    private Integer dailyCalories;

    @ElementCollection
    @CollectionTable(name = "diet_plan_items",
            joinColumns = @JoinColumn(name = "diet_id"))
    @Column(name = "plan_item")
    private List<String> dietPlan;

    private String aiGeneratedPlan;
    private LocalDate createdDate;

    @ManyToOne
    private User user;
}
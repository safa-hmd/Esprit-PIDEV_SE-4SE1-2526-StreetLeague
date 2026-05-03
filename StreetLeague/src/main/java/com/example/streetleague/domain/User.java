package com.example.streetleague.domain;

import com.example.streetleague.Entity.*;

import com.example.streetleague.Entity.Comment;
import com.example.streetleague.Entity.Post;
import com.example.streetleague.Entity.waterReminder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter          // ← @Getter + @Setter au lieu de @Data
@Setter          // ← @Data cause des conflits avec @Builder
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idUser;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Builder.Default
    private boolean enabled = true;

    // ── Relations team ────────────────────────────────────────
    @OneToMany(mappedBy = "captain", cascade = CascadeType.ALL)
    @JsonIgnore
    List<Team> captainedTeams;

    @ManyToMany(mappedBy = "players")
    @JsonIgnore
    List<Team> teams;

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    List<Training> trainings;

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    List<Match> createdMatches;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;


    @Column(name = "team_id")
    Long teamId;

    // ── Nouvelles relations (newsHealth) ──────────────────────
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore

    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<waterReminder> waterReminders;

    // ── Health fields ──
    private Double weight;
    private Double height;
    private Double bmi;

    // ── New relations ──
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DailyWaterLog> waterLogs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserGoal> goals;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserBadge> badges;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private SpinResult spinResult;
}

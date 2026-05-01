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
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long idUser;

    @Column(nullable = false)
    String fullName;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    Role role;

    @Builder.Default
    boolean enabled = true;

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
    String resetToken;

    @Column(name = "reset_token_expiry")
    LocalDateTime resetTokenExpiry;


    @Column(name = "team_id")
    Long teamId;

    // ── Nouvelles relations (newsHealth) ──────────────────────
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    List<waterReminder> waterReminders;

}
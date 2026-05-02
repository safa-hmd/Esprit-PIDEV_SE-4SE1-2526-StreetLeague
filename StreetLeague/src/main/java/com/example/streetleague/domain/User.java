package com.example.streetleague.domain;

<<<<<<< HEAD
=======
import com.example.streetleague.Entity.*;

import com.example.streetleague.Entity.Comment;
import com.example.streetleague.Entity.Post;
import com.example.streetleague.Entity.waterReminder;

>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
<<<<<<< HEAD
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
=======
@Getter          // ← @Getter + @Setter au lieu de @Data
@Setter          // ← @Data cause des conflits avec @Builder
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5
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
<<<<<<< HEAD
    private boolean enabled = true;
=======
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
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
<<<<<<< HEAD
    private LocalDateTime resetTokenExpiry;

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }
}
=======
    LocalDateTime resetTokenExpiry;


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
>>>>>>> d97c24f7ac7e148ae108ca34ae4d7f2e7dd375a5

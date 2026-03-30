package com.example.streetleague.domain;

import com.example.streetleague.Entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
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

    // ← AJOUT: colonne team_id directe dans la table users
    @Column(name = "team_id")
    Long teamId;

    // ===== EXPLICIT GETTERS (Lombok not processing correctly) =====
    
    public Long getIdUser() {
        return this.idUser;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public Role getRole() {
        return this.role;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public List<Team> getCaptainedTeams() {
        return this.captainedTeams;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public List<Training> getTrainings() {
        return this.trainings;
    }

    public List<Match> getCreatedMatches() {
        return this.createdMatches;
    }

    public String getResetToken() {
        return this.resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return this.resetTokenExpiry;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    // ===== EXPLICIT SETTERS =====

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCaptainedTeams(List<Team> captainedTeams) {
        this.captainedTeams = captainedTeams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    public void setCreatedMatches(List<Match> createdMatches) {
        this.createdMatches = createdMatches;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    // ===== STATIC BUILDER HELPER =====
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long idUser;
        private String fullName;
        private String email;
        private String password;
        private Role role;
        private boolean enabled = true;
        private List<Team> captainedTeams;
        private List<Team> teams;
        private List<Training> trainings;
        private List<Match> createdMatches;
        private String resetToken;
        private LocalDateTime resetTokenExpiry;
        private Long teamId;

        public UserBuilder idUser(Long idUser) { this.idUser = idUser; return this; }
        public UserBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public UserBuilder captainedTeams(List<Team> captainedTeams) { this.captainedTeams = captainedTeams; return this; }
        public UserBuilder teams(List<Team> teams) { this.teams = teams; return this; }
        public UserBuilder trainings(List<Training> trainings) { this.trainings = trainings; return this; }
        public UserBuilder createdMatches(List<Match> createdMatches) { this.createdMatches = createdMatches; return this; }
        public UserBuilder resetToken(String resetToken) { this.resetToken = resetToken; return this; }
        public UserBuilder resetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; return this; }
        public UserBuilder teamId(Long teamId) { this.teamId = teamId; return this; }

        public User build() {
            User user = new User();
            user.setIdUser(idUser);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            user.setEnabled(enabled);
            user.setCaptainedTeams(captainedTeams);
            user.setTeams(teams);
            user.setTrainings(trainings);
            user.setCreatedMatches(createdMatches);
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(resetTokenExpiry);
            user.setTeamId(teamId);
            return user;
        }
    }
}
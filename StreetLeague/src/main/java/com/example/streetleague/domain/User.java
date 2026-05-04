package com.example.streetleague.domain;

import com.example.streetleague.Entity.*;

import com.example.streetleague.Entity.Comment;
import com.example.streetleague.Entity.Post;
import com.example.streetleague.Entity.waterReminder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
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

    @Column(nullable = false)
    int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    Role role;

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


    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Team> getCaptainedTeams() {
        return captainedTeams;
    }

    public void setCaptainedTeams(List<Team> captainedTeams) {
        this.captainedTeams = captainedTeams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    public List<Match> getCreatedMatches() {
        return createdMatches;
    }

    public void setCreatedMatches(List<Match> createdMatches) {
        this.createdMatches = createdMatches;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<waterReminder> getWaterReminders() {
        return waterReminders;
    }

    public void setWaterReminders(List<waterReminder> waterReminders) {
        this.waterReminders = waterReminders;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public List<DailyWaterLog> getWaterLogs() {
        return waterLogs;
    }

    public void setWaterLogs(List<DailyWaterLog> waterLogs) {
        this.waterLogs = waterLogs;
    }

    public List<UserGoal> getGoals() {
        return goals;
    }

    public void setGoals(List<UserGoal> goals) {
        this.goals = goals;
    }

    public List<UserBadge> getBadges() {
        return badges;
    }

    public void setBadges(List<UserBadge> badges) {
        this.badges = badges;
    }

    public SpinResult getSpinResult() {
        return spinResult;
    }

    public void setSpinResult(SpinResult spinResult) {
        this.spinResult = spinResult;
    }

    public User(Long idUser, String fullName, String email, String password, int age, Role role, boolean enabled, List<Team> captainedTeams, List<Team> teams, List<Training> trainings, List<Match> createdMatches, String resetToken, LocalDateTime resetTokenExpiry, Long teamId, List<Post> posts, List<Comment> comments, List<waterReminder> waterReminders, Double weight, Double height, Double bmi, List<DailyWaterLog> waterLogs, List<UserGoal> goals, List<UserBadge> badges, SpinResult spinResult) {
        this.idUser = idUser;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.age = age;
        this.role = role;
        this.enabled = enabled;
        this.captainedTeams = captainedTeams;
        this.teams = teams;
        this.trainings = trainings;
        this.createdMatches = createdMatches;
        this.resetToken = resetToken;
        this.resetTokenExpiry = resetTokenExpiry;
        this.teamId = teamId;
        this.posts = posts;
        this.comments = comments;
        this.waterReminders = waterReminders;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.waterLogs = waterLogs;
        this.goals = goals;
        this.badges = badges;
        this.spinResult = spinResult;
    }

    public User() {
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long idUser;
        private String fullName;
        private String email;
        private String password;
        private int age;
        private Role role;
        private boolean enabled = true;
        private List<Team> captainedTeams;
        private List<Team> teams;
        private List<Training> trainings;
        private List<Match> createdMatches;
        private String resetToken;
        private LocalDateTime resetTokenExpiry;
        private Long teamId;
        private List<Post> posts;
        private List<Comment> comments;
        private List<waterReminder> waterReminders;
        private Double weight;
        private Double height;
        private Double bmi;
        private List<DailyWaterLog> waterLogs;
        private List<UserGoal> goals;
        private List<UserBadge> badges;
        private SpinResult spinResult;

        public UserBuilder idUser(Long idUser) {
            this.idUser = idUser;
            return this;
        }

        public UserBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        public UserBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserBuilder captainedTeams(List<Team> captainedTeams) {
            this.captainedTeams = captainedTeams;
            return this;
        }

        public UserBuilder teams(List<Team> teams) {
            this.teams = teams;
            return this;
        }

        public UserBuilder trainings(List<Training> trainings) {
            this.trainings = trainings;
            return this;
        }

        public UserBuilder createdMatches(List<Match> createdMatches) {
            this.createdMatches = createdMatches;
            return this;
        }

        public UserBuilder resetToken(String resetToken) {
            this.resetToken = resetToken;
            return this;
        }

        public UserBuilder resetTokenExpiry(LocalDateTime resetTokenExpiry) {
            this.resetTokenExpiry = resetTokenExpiry;
            return this;
        }

        public UserBuilder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }

        public UserBuilder posts(List<Post> posts) {
            this.posts = posts;
            return this;
        }

        public UserBuilder comments(List<Comment> comments) {
            this.comments = comments;
            return this;
        }

        public UserBuilder waterReminders(List<waterReminder> waterReminders) {
            this.waterReminders = waterReminders;
            return this;
        }

        public UserBuilder weight(Double weight) {
            this.weight = weight;
            return this;
        }

        public UserBuilder height(Double height) {
            this.height = height;
            return this;
        }

        public UserBuilder bmi(Double bmi) {
            this.bmi = bmi;
            return this;
        }

        public UserBuilder waterLogs(List<DailyWaterLog> waterLogs) {
            this.waterLogs = waterLogs;
            return this;
        }

        public UserBuilder goals(List<UserGoal> goals) {
            this.goals = goals;
            return this;
        }

        public UserBuilder badges(List<UserBadge> badges) {
            this.badges = badges;
            return this;
        }

        public UserBuilder spinResult(SpinResult spinResult) {
            this.spinResult = spinResult;
            return this;
        }

        public User build() {
            return new User(idUser, fullName, email, password, age, role, enabled, captainedTeams, teams, trainings, createdMatches, resetToken, resetTokenExpiry, teamId, posts, comments, waterReminders, weight, height, bmi, waterLogs, goals, badges, spinResult);
        }
    }
}


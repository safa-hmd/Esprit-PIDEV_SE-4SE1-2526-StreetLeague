package com.example.streetleague.domain;

import com.example.streetleague.Entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
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


}
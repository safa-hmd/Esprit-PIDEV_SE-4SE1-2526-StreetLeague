package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "matchs")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idMatch;

    LocalDateTime matchDate;
    String location;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    MatchStatus status;

    Integer scoreTeamA;
    Integer scoreTeamB;

    private boolean statsUpdated = false;

    // Team A
    @ManyToOne
    @JoinColumn(name = "teamA_id", nullable = false)
    Team teamA;

    // Team B
    @ManyToOne
    @JoinColumn(name = "teamB_id", nullable = false)
    Team teamB;

    // The captain (PLAYER) who created/sent the match request
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    User createdBy;
}
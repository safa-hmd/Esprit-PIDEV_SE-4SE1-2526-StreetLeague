package com.example.streetleague.Entity;


import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tournament_registrations")
public class TournamentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    // Si tournoi INDIVIDUAL → player renseigné, team null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private User player;

    // Si tournoi TEAM → team renseigné, player null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    public void prePersist() {
        this.registeredAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RegistrationStatus.PENDING;
        }
    }

    public TournamentRegistration() {
    }

    public TournamentRegistration(Long id, Tournament tournament, User player, Team team, RegistrationStatus status, LocalDateTime registeredAt) {
        this.id = id;
        this.tournament = tournament;
        this.player = player;
        this.team = team;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentRegistration that = (TournamentRegistration) o;
        return Objects.equals(id, that.id) && Objects.equals(tournament, that.tournament) && Objects.equals(player, that.player) && Objects.equals(team, that.team) && status == that.status && Objects.equals(registeredAt, that.registeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournament, player, team, status, registeredAt);
    }

    @Override
    public String toString() {
        return "TournamentRegistration{" +
                "id=" + id +
                ", tournament=" + tournament +
                ", player=" + player +
                ", team=" + team +
                ", status=" + status +
                ", registeredAt=" + registeredAt +
                '}';
    }

    public static TournamentRegistrationBuilder builder() {
        return new TournamentRegistrationBuilder();
    }

    public static class TournamentRegistrationBuilder {
        private Long id;
        private Tournament tournament;
        private User player;
        private Team team;
        private RegistrationStatus status;
        private LocalDateTime registeredAt;

        public TournamentRegistrationBuilder id(Long id) { this.id = id; return this; }
        public TournamentRegistrationBuilder tournament(Tournament tournament) { this.tournament = tournament; return this; }
        public TournamentRegistrationBuilder player(User player) { this.player = player; return this; }
        public TournamentRegistrationBuilder team(Team team) { this.team = team; return this; }
        public TournamentRegistrationBuilder status(RegistrationStatus status) { this.status = status; return this; }
        public TournamentRegistrationBuilder registeredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; return this; }

        public TournamentRegistration build() {
            return new TournamentRegistration(id, tournament, player, team, status, registeredAt);
        }
    }
}


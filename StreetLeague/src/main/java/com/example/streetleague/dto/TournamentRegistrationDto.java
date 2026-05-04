package com.example.streetleague.dto;

import com.example.streetleague.Entity.RegistrationStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public class TournamentRegistrationDto {

    private Long id;
    private Long tournamentId;
    private String tournamentName;

    // Renseigné si tournoi INDIVIDUAL
    private Long playerId;
    private String playerUsername;

    // Renseigné si tournoi TEAM
    private Long teamId;
    private String teamName;

    private RegistrationStatus status;
    private LocalDateTime registeredAt;

    public TournamentRegistrationDto() {
    }

    public TournamentRegistrationDto(Long id, Long tournamentId, String tournamentName, Long playerId, String playerUsername, Long teamId, String teamName, RegistrationStatus status, LocalDateTime registeredAt) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.playerId = playerId;
        this.playerUsername = playerUsername;
        this.teamId = teamId;
        this.teamName = teamName;
        this.status = status;
        this.registeredAt = registeredAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public String getPlayerUsername() { return playerUsername; }
    public void setPlayerUsername(String playerUsername) { this.playerUsername = playerUsername; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentRegistrationDto that = (TournamentRegistrationDto) o;
        return Objects.equals(id, that.id) && Objects.equals(tournamentId, that.tournamentId) && Objects.equals(playerId, that.playerId) && Objects.equals(teamId, that.teamId) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournamentId, playerId, teamId, status);
    }

    @Override
    public String toString() {
        return "TournamentRegistrationDto{" +
                "id=" + id +
                ", tournamentName='" + tournamentName + '\'' +
                ", status=" + status +
                '}';
    }

    public static TournamentRegistrationDtoBuilder builder() {
        return new TournamentRegistrationDtoBuilder();
    }

    public static class TournamentRegistrationDtoBuilder {
        private Long id;
        private Long tournamentId;
        private String tournamentName;
        private Long playerId;
        private String playerUsername;
        private Long teamId;
        private String teamName;
        private RegistrationStatus status;
        private LocalDateTime registeredAt;

        public TournamentRegistrationDtoBuilder id(Long id) { this.id = id; return this; }
        public TournamentRegistrationDtoBuilder tournamentId(Long tournamentId) { this.tournamentId = tournamentId; return this; }
        public TournamentRegistrationDtoBuilder tournamentName(String tournamentName) { this.tournamentName = tournamentName; return this; }
        public TournamentRegistrationDtoBuilder playerId(Long playerId) { this.playerId = playerId; return this; }
        public TournamentRegistrationDtoBuilder playerUsername(String playerUsername) { this.playerUsername = playerUsername; return this; }
        public TournamentRegistrationDtoBuilder teamId(Long teamId) { this.teamId = teamId; return this; }
        public TournamentRegistrationDtoBuilder teamName(String teamName) { this.teamName = teamName; return this; }
        public TournamentRegistrationDtoBuilder status(RegistrationStatus status) { this.status = status; return this; }
        public TournamentRegistrationDtoBuilder registeredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; return this; }

        public TournamentRegistrationDto build() {
            return new TournamentRegistrationDto(id, tournamentId, tournamentName, playerId, playerUsername, teamId, teamName, status, registeredAt);
        }
    }
}

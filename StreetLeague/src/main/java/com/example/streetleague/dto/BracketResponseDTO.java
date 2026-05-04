package com.example.streetleague.dto;

import com.example.streetleague.Entity.BracketType;
import com.example.streetleague.Entity.TournamentMatch;
import com.example.streetleague.Entity.TournamentMatchStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BracketResponseDTO {

    private Long tournamentId;
    private String tournamentName;
    private BracketType bracketType;
    private int totalRounds;
    private Map<Integer, List<MatchSlotDTO>> rounds; // round → liste de matchs

    public BracketResponseDTO() {
    }

    public BracketResponseDTO(Long tournamentId, String tournamentName, BracketType bracketType, int totalRounds, Map<Integer, List<MatchSlotDTO>> rounds) {
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.bracketType = bracketType;
        this.totalRounds = totalRounds;
        this.rounds = rounds;
    }

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public BracketType getBracketType() { return bracketType; }
    public void setBracketType(BracketType bracketType) { this.bracketType = bracketType; }

    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }

    public Map<Integer, List<MatchSlotDTO>> getRounds() { return rounds; }
    public void setRounds(Map<Integer, List<MatchSlotDTO>> rounds) { this.rounds = rounds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BracketResponseDTO that = (BracketResponseDTO) o;
        return totalRounds == that.totalRounds && Objects.equals(tournamentId, that.tournamentId) && Objects.equals(tournamentName, that.tournamentName) && bracketType == that.bracketType && Objects.equals(rounds, that.rounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tournamentId, tournamentName, bracketType, totalRounds, rounds);
    }

    @Override
    public String toString() {
        return "BracketResponseDTO{" +
                "tournamentId=" + tournamentId +
                ", tournamentName='" + tournamentName + '\'' +
                ", bracketType=" + bracketType +
                ", totalRounds=" + totalRounds +
                ", rounds=" + rounds +
                '}';
    }

    public static BracketResponseDTO from(Long tournamentId, String name, List<TournamentMatch> matches) {
        if (matches.isEmpty()) throw new IllegalArgumentException("Empty bracket");

        BracketType type = matches.get(0).getBracketType();
        int maxRound = matches.stream().mapToInt(TournamentMatch::getRound).max().orElse(0);

        Map<Integer, List<MatchSlotDTO>> rounds = matches.stream()
                .collect(Collectors.groupingBy(
                        TournamentMatch::getRound,
                        Collectors.mapping(MatchSlotDTO::from, Collectors.toList())
                ));

        return BracketResponseDTO.builder()
                .tournamentId(tournamentId)
                .tournamentName(name)
                .bracketType(type)
                .totalRounds(maxRound)
                .rounds(rounds)
                .build();
    }

    public static BracketResponseDTOBuilder builder() {
        return new BracketResponseDTOBuilder();
    }

    public static class BracketResponseDTOBuilder {
        private Long tournamentId;
        private String tournamentName;
        private BracketType bracketType;
        private int totalRounds;
        private Map<Integer, List<MatchSlotDTO>> rounds;

        public BracketResponseDTOBuilder tournamentId(Long tournamentId) { this.tournamentId = tournamentId; return this; }
        public BracketResponseDTOBuilder tournamentName(String tournamentName) { this.tournamentName = tournamentName; return this; }
        public BracketResponseDTOBuilder bracketType(BracketType bracketType) { this.bracketType = bracketType; return this; }
        public BracketResponseDTOBuilder totalRounds(int totalRounds) { this.totalRounds = totalRounds; return this; }
        public BracketResponseDTOBuilder rounds(Map<Integer, List<MatchSlotDTO>> rounds) { this.rounds = rounds; return this; }

        public BracketResponseDTO build() {
            return new BracketResponseDTO(tournamentId, tournamentName, bracketType, totalRounds, rounds);
        }
    }

    // ── DTO d'un slot de match ──
    public static class MatchSlotDTO {
        private Long id;
        private int round;
        private int position;
        private TournamentMatchStatus status;
        private ParticipantDTO participantA;
        private ParticipantDTO participantB;
        private ParticipantDTO winner;
        private LocalDateTime matchDate;
        private String location;
        private Long nextMatchId;
        private String nextMatchSlot;

        public MatchSlotDTO() {
        }

        public MatchSlotDTO(Long id, int round, int position, TournamentMatchStatus status, ParticipantDTO participantA, ParticipantDTO participantB, ParticipantDTO winner, LocalDateTime matchDate, String location, Long nextMatchId, String nextMatchSlot) {
            this.id = id;
            this.round = round;
            this.position = position;
            this.status = status;
            this.participantA = participantA;
            this.participantB = participantB;
            this.winner = winner;
            this.matchDate = matchDate;
            this.location = location;
            this.nextMatchId = nextMatchId;
            this.nextMatchSlot = nextMatchSlot;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public int getRound() { return round; }
        public void setRound(int round) { this.round = round; }

        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }

        public TournamentMatchStatus getStatus() { return status; }
        public void setStatus(TournamentMatchStatus status) { this.status = status; }

        public ParticipantDTO getParticipantA() { return participantA; }
        public void setParticipantA(ParticipantDTO participantA) { this.participantA = participantA; }

        public ParticipantDTO getParticipantB() { return participantB; }
        public void setParticipantB(ParticipantDTO participantB) { this.participantB = participantB; }

        public ParticipantDTO getWinner() { return winner; }
        public void setWinner(ParticipantDTO winner) { this.winner = winner; }

        public LocalDateTime getMatchDate() { return matchDate; }
        public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Long getNextMatchId() { return nextMatchId; }
        public void setNextMatchId(Long nextMatchId) { this.nextMatchId = nextMatchId; }

        public String getNextMatchSlot() { return nextMatchSlot; }
        public void setNextMatchSlot(String nextMatchSlot) { this.nextMatchSlot = nextMatchSlot; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MatchSlotDTO that = (MatchSlotDTO) o;
            return round == that.round && position == that.position && Objects.equals(id, that.id) && status == that.status && Objects.equals(participantA, that.participantA) && Objects.equals(participantB, that.participantB) && Objects.equals(winner, that.winner) && Objects.equals(matchDate, that.matchDate) && Objects.equals(location, that.location) && Objects.equals(nextMatchId, that.nextMatchId) && Objects.equals(nextMatchSlot, that.nextMatchSlot);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, round, position, status, participantA, participantB, winner, matchDate, location, nextMatchId, nextMatchSlot);
        }

        @Override
        public String toString() {
            return "MatchSlotDTO{" +
                    "id=" + id +
                    ", round=" + round +
                    ", position=" + position +
                    ", status=" + status +
                    ", matchDate=" + matchDate +
                    ", location='" + location + '\'' +
                    '}';
        }

        public static MatchSlotDTO from(TournamentMatch tm) {
            MatchSlotDTOBuilder b = MatchSlotDTO.builder()
                    .id(tm.getId())
                    .round(tm.getRound())
                    .position(tm.getPosition())
                    .status(tm.getStatus())
                    .nextMatchId(tm.getNextMatch() != null ? tm.getNextMatch().getId() : null)
                    .nextMatchSlot(tm.getNextMatchSlot());

            if (tm.getMatch() != null) {
                b.matchDate(tm.getMatch().getMatchDate());
                b.location(tm.getMatch().getLocation());
            }

            boolean isTeamMatch = tm.getPlayer1() == null && tm.getPlayer2() == null;
            if (isTeamMatch && tm.getMatch() != null) {
                if (tm.getMatch().getTeamA() != null)
                    b.participantA(ParticipantDTO.fromTeam(tm.getMatch().getTeamA()));
                if (tm.getMatch().getTeamB() != null)
                    b.participantB(ParticipantDTO.fromTeam(tm.getMatch().getTeamB()));
                if (tm.getWinnerTeam() != null)
                    b.winner(ParticipantDTO.fromTeam(tm.getWinnerTeam()));
            } else {
                if (tm.getPlayer1() != null)
                    b.participantA(ParticipantDTO.fromUser(tm.getPlayer1()));
                if (tm.getPlayer2() != null)
                    b.participantB(ParticipantDTO.fromUser(tm.getPlayer2()));
                if (tm.getWinnerPlayer() != null)
                    b.winner(ParticipantDTO.fromUser(tm.getWinnerPlayer()));
            }

            return b.build();
        }

        public static MatchSlotDTOBuilder builder() {
            return new MatchSlotDTOBuilder();
        }

        public static class MatchSlotDTOBuilder {
            private Long id;
            private int round;
            private int position;
            private TournamentMatchStatus status;
            private ParticipantDTO participantA;
            private ParticipantDTO participantB;
            private ParticipantDTO winner;
            private LocalDateTime matchDate;
            private String location;
            private Long nextMatchId;
            private String nextMatchSlot;

            public MatchSlotDTOBuilder id(Long id) { this.id = id; return this; }
            public MatchSlotDTOBuilder round(int round) { this.round = round; return this; }
            public MatchSlotDTOBuilder position(int position) { this.position = position; return this; }
            public MatchSlotDTOBuilder status(TournamentMatchStatus status) { this.status = status; return this; }
            public MatchSlotDTOBuilder participantA(ParticipantDTO participantA) { this.participantA = participantA; return this; }
            public MatchSlotDTOBuilder participantB(ParticipantDTO participantB) { this.participantB = participantB; return this; }
            public MatchSlotDTOBuilder winner(ParticipantDTO winner) { this.winner = winner; return this; }
            public MatchSlotDTOBuilder matchDate(LocalDateTime matchDate) { this.matchDate = matchDate; return this; }
            public MatchSlotDTOBuilder location(String location) { this.location = location; return this; }
            public MatchSlotDTOBuilder nextMatchId(Long nextMatchId) { this.nextMatchId = nextMatchId; return this; }
            public MatchSlotDTOBuilder nextMatchSlot(String nextMatchSlot) { this.nextMatchSlot = nextMatchSlot; return this; }

            public MatchSlotDTO build() {
                return new MatchSlotDTO(id, round, position, status, participantA, participantB, winner, matchDate, location, nextMatchId, nextMatchSlot);
            }
        }
    }

    public static class ParticipantDTO {
        private Long id;
        private String name;
        private String type; // "TEAM" ou "PLAYER"

        public ParticipantDTO() {
        }

        public ParticipantDTO(Long id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParticipantDTO that = (ParticipantDTO) o;
            return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, type);
        }

        @Override
        public String toString() {
            return "ParticipantDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        public static ParticipantDTO fromTeam(com.example.streetleague.Entity.Team t) {
            return ParticipantDTO.builder()
                    .id(t.getIdTeam())
                    .name(t.getName())
                    .type("TEAM")
                    .build();
        }

        public static ParticipantDTO fromUser(com.example.streetleague.domain.User u) {
            return ParticipantDTO.builder()
                    .id(u.getIdUser())
                    .name(u.getFullName())
                    .type("PLAYER")
                    .build();
        }

        public static ParticipantDTOBuilder builder() {
            return new ParticipantDTOBuilder();
        }

        public static class ParticipantDTOBuilder {
            private Long id;
            private String name;
            private String type;

            public ParticipantDTOBuilder id(Long id) { this.id = id; return this; }
            public ParticipantDTOBuilder name(String name) { this.name = name; return this; }
            public ParticipantDTOBuilder type(String type) { this.type = type; return this; }

            public ParticipantDTO build() {
                return new ParticipantDTO(id, name, type);
            }
        }
    }
}
package com.example.streetleague.dto;

import com.example.streetleague.Entity.BracketType;
import com.example.streetleague.Entity.TournamentMatch;
import com.example.streetleague.Entity.TournamentMatchStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
public class BracketResponseDTO {

    private Long tournamentId;
    private String tournamentName;
    private BracketType bracketType;
    private int totalRounds;
    private Map<Integer, List<MatchSlotDTO>> rounds; // round → liste de matchs

    // ── Conversion depuis List<TournamentMatch> ──
    public static BracketResponseDTO from(Long tournamentId, String name,
                                          List<TournamentMatch> matches) {
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

    // ── DTO d'un slot de match ──
    @Data
    @Builder
    public static class MatchSlotDTO {
        private Long id;
        private int round;
        private int position;
        private TournamentMatchStatus status;

        // Participants (Team ou Player)
        private ParticipantDTO participantA;
        private ParticipantDTO participantB;
        private ParticipantDTO winner;

        private LocalDateTime matchDate;
        private String location;

        // Lien vers le prochain match dans le bracket
        private Long nextMatchId;
        private String nextMatchSlot;

        public static MatchSlotDTO from(TournamentMatch tm) {
            MatchSlotDTO.MatchSlotDTOBuilder b = MatchSlotDTO.builder()
                    .id(tm.getId())
                    .round(tm.getRound())
                    .position(tm.getPosition())
                    .status(tm.getStatus())
                    .nextMatchId(tm.getNextMatch() != null ? tm.getNextMatch().getId() : null)
                    .nextMatchSlot(tm.getNextMatchSlot());

            // Date & location depuis le Match JPA
            if (tm.getMatch() != null) {
                b.matchDate(tm.getMatch().getMatchDate());
                b.location(tm.getMatch().getLocation());
            }

            // Participants
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
    }

    @Data
    @Builder
    public static class ParticipantDTO {
        private Long id;
        private String name;
        private String type; // "TEAM" ou "PLAYER"

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
    }
}
package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tournament_matches")
public class TournamentMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers le tournoi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    // Lien vers le Match réel (de la collègue)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "match_id")
    private Match match;

    // ── Bracket metadata ──
    @Column(nullable = false)
    private int round;           // 1 = premier round, 2 = demi, etc.

    @Column(nullable = false)
    private int position;        // position dans le round (0-indexed)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BracketType bracketType;   // SINGLE_ELIMINATION ou ROUND_ROBIN

    // ── Pour tournois INDIVIDUAL : player1/player2 ──
    // (teamA/teamB restent null dans Match)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id")
    private User player1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    private User player2;

    // ── Avancement bracket ──
    // Référence au TournamentMatch suivant (parent dans l'arbre)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_match_id")
    private TournamentMatch nextMatch;

    // Slot dans le nextMatch : "A" ou "B" (teamA ou teamB du prochain match)
    @Column(length = 1)
    private String nextMatchSlot;   // "A" ou "B"

    // Vainqueur (Team ou User selon type)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_player_id")
    private User winnerPlayer;

    @Enumerated(EnumType.STRING)
    private TournamentMatchStatus status = TournamentMatchStatus.PENDING;

    public TournamentMatch() {
    }

    public TournamentMatch(Long id, Tournament tournament, Match match, int round, int position, BracketType bracketType, User player1, User player2, TournamentMatch nextMatch, String nextMatchSlot, Team winnerTeam, User winnerPlayer, TournamentMatchStatus status) {
        this.id = id;
        this.tournament = tournament;
        this.match = match;
        this.round = round;
        this.position = position;
        this.bracketType = bracketType;
        this.player1 = player1;
        this.player2 = player2;
        this.nextMatch = nextMatch;
        this.nextMatchSlot = nextMatchSlot;
        this.winnerTeam = winnerTeam;
        this.winnerPlayer = winnerPlayer;
        this.status = status;
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

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public BracketType getBracketType() {
        return bracketType;
    }

    public void setBracketType(BracketType bracketType) {
        this.bracketType = bracketType;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public TournamentMatch getNextMatch() {
        return nextMatch;
    }

    public void setNextMatch(TournamentMatch nextMatch) {
        this.nextMatch = nextMatch;
    }

    public String getNextMatchSlot() {
        return nextMatchSlot;
    }

    public void setNextMatchSlot(String nextMatchSlot) {
        this.nextMatchSlot = nextMatchSlot;
    }

    public Team getWinnerTeam() {
        return winnerTeam;
    }

    public void setWinnerTeam(Team winnerTeam) {
        this.winnerTeam = winnerTeam;
    }

    public User getWinnerPlayer() {
        return winnerPlayer;
    }

    public void setWinnerPlayer(User winnerPlayer) {
        this.winnerPlayer = winnerPlayer;
    }

    public TournamentMatchStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentMatchStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentMatch that = (TournamentMatch) o;
        return round == that.round && position == that.position && Objects.equals(id, that.id) && Objects.equals(tournament, that.tournament) && Objects.equals(match, that.match) && bracketType == that.bracketType && Objects.equals(player1, that.player1) && Objects.equals(player2, that.player2) && Objects.equals(nextMatch, that.nextMatch) && Objects.equals(nextMatchSlot, that.nextMatchSlot) && Objects.equals(winnerTeam, that.winnerTeam) && Objects.equals(winnerPlayer, that.winnerPlayer) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournament, match, round, position, bracketType, player1, player2, nextMatch, nextMatchSlot, winnerTeam, winnerPlayer, status);
    }

    @Override
    public String toString() {
        return "TournamentMatch{" +
                "id=" + id +
                ", tournament=" + tournament +
                ", match=" + match +
                ", round=" + round +
                ", position=" + position +
                ", bracketType=" + bracketType +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", nextMatch=" + nextMatch +
                ", nextMatchSlot='" + nextMatchSlot + '\'' +
                ", winnerTeam=" + winnerTeam +
                ", winnerPlayer=" + winnerPlayer +
                ", status=" + status +
                '}';
    }

    public static TournamentMatchBuilder builder() {
        return new TournamentMatchBuilder();
    }

    public static class TournamentMatchBuilder {
        private Long id;
        private Tournament tournament;
        private Match match;
        private int round;
        private int position;
        private BracketType bracketType;
        private User player1;
        private User player2;
        private TournamentMatch nextMatch;
        private String nextMatchSlot;
        private Team winnerTeam;
        private User winnerPlayer;
        private TournamentMatchStatus status = TournamentMatchStatus.PENDING;

        public TournamentMatchBuilder id(Long id) { this.id = id; return this; }
        public TournamentMatchBuilder tournament(Tournament tournament) { this.tournament = tournament; return this; }
        public TournamentMatchBuilder match(Match match) { this.match = match; return this; }
        public TournamentMatchBuilder round(int round) { this.round = round; return this; }
        public TournamentMatchBuilder position(int position) { this.position = position; return this; }
        public TournamentMatchBuilder bracketType(BracketType bracketType) { this.bracketType = bracketType; return this; }
        public TournamentMatchBuilder player1(User player1) { this.player1 = player1; return this; }
        public TournamentMatchBuilder player2(User player2) { this.player2 = player2; return this; }
        public TournamentMatchBuilder nextMatch(TournamentMatch nextMatch) { this.nextMatch = nextMatch; return this; }
        public TournamentMatchBuilder nextMatchSlot(String nextMatchSlot) { this.nextMatchSlot = nextMatchSlot; return this; }
        public TournamentMatchBuilder winnerTeam(Team winnerTeam) { this.winnerTeam = winnerTeam; return this; }
        public TournamentMatchBuilder winnerPlayer(User winnerPlayer) { this.winnerPlayer = winnerPlayer; return this; }
        public TournamentMatchBuilder status(TournamentMatchStatus status) { this.status = status; return this; }

        public TournamentMatch build() {
            return new TournamentMatch(id, tournament, match, round, position, bracketType, player1, player2, nextMatch, nextMatchSlot, winnerTeam, winnerPlayer, status);
        }
    }
}
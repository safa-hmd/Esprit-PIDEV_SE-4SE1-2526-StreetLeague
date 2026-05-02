package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tournament_matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private TournamentMatchStatus status = TournamentMatchStatus.PENDING;
}
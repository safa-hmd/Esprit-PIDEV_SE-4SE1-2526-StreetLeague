package com.example.streetleague;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.BracketgeneratorserviceImpl;
import com.example.streetleague.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BracketGeneratorServiceImplTest {

    @Mock TournamentRepository tournamentRepository;
    @Mock TournamentMatchRepository tournamentMatchRepository;
    @Mock TournamentRegistrationRepository registrationRepository;
    @Mock MatchRepository matchRepository;

    @InjectMocks
    BracketgeneratorserviceImpl bracketService;

    private Tournament teamTournament;
    private Tournament individualTournament;

    @BeforeEach
    void setUp() {
        teamTournament = Tournament.builder()
                .id(1L).name("Cup").tournamentType(TournamentType.TEAM)
                .startDate(LocalDate.now().plusDays(3))
                .endDate(LocalDate.now().plusDays(10))
                .location("Tunis")
                .build();

        individualTournament = Tournament.builder()
                .id(2L).name("Solo Cup").tournamentType(TournamentType.INDIVIDUAL)
                .startDate(LocalDate.now().plusDays(3))
                .endDate(LocalDate.now().plusDays(10))
                .location("Sfax")
                .build();
    }

    // ── Helpers ──

    private List<TournamentRegistration> makeTeamRegistrations(int count) {
        List<TournamentRegistration> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Team team = new Team(); team.setIdTeam((long) i + 1); team.setName("Team" + i);
            list.add(TournamentRegistration.builder()
                    .team(team).status(RegistrationStatus.CONFIRMED).build());
        }
        return list;
    }

    private List<TournamentRegistration> makePlayerRegistrations(int count) {
        List<TournamentRegistration> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User(); user.setIdUser((long) i + 1); user.setFullName("Player" + i);
            list.add(TournamentRegistration.builder()
                    .player(user).status(RegistrationStatus.CONFIRMED).build());
        }
        return list;
    }

    private void mockGenerate(Tournament t, List<TournamentRegistration> regs) {
        when(tournamentRepository.findById(t.getId())).thenReturn(Optional.of(t));
        when(tournamentMatchRepository.existsByTournamentId(t.getId())).thenReturn(false);
        when(registrationRepository.findByTournamentIdAndStatus(t.getId(), RegistrationStatus.CONFIRMED))
                .thenReturn(regs);
        when(tournamentMatchRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
        when(tournamentMatchRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    // ════════════════════════════════════════════════
    //  SINGLE ELIMINATION — TEAM
    // ════════════════════════════════════════════════

    @Test
    void singleElim_4teams_generates3Matches() {
        List<TournamentRegistration> regs = makeTeamRegistrations(4);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.SINGLE_ELIMINATION);

        // 4 équipes → 2 matchs R1 + 1 finale = 3
        assertThat(result).hasSize(3);
    }

    @Test
    void singleElim_8teams_generates7Matches() {
        List<TournamentRegistration> regs = makeTeamRegistrations(8);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.SINGLE_ELIMINATION);

        // 8 → 4 + 2 + 1 = 7
        assertThat(result).hasSize(7);
    }

    @Test
    void singleElim_roundsAreCorrectlyNumbered() {
        List<TournamentRegistration> regs = makeTeamRegistrations(4);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.SINGLE_ELIMINATION);

        assertThat(result).anyMatch(m -> m.getRound() == 1);
        assertThat(result).anyMatch(m -> m.getRound() == 2);
    }

    @Test
    void singleElim_5teams_handlesByeCorrectly() {
        List<TournamentRegistration> regs = makeTeamRegistrations(5);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.SINGLE_ELIMINATION);

        // 5 → next power of 2 = 8 → 7 matchs, 3 BYEs au R1
        long byes = result.stream().filter(m -> m.getStatus() == TournamentMatchStatus.BYE).count();
        assertThat(byes).isGreaterThan(0);
    }

    @Test
    void singleElim_allMatchesHaveBracketTypeSingleElim() {
        List<TournamentRegistration> regs = makeTeamRegistrations(4);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.SINGLE_ELIMINATION);

        assertThat(result).allMatch(m -> m.getBracketType() == BracketType.SINGLE_ELIMINATION);
    }

    @Test
    void singleElim_tournamentNotFound_throws() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bracketService.generateBracket(99L, BracketType.SINGLE_ELIMINATION))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void singleElim_lessThan2Participants_throws() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(teamTournament));
        when(tournamentMatchRepository.existsByTournamentId(1L)).thenReturn(false);
        when(registrationRepository.findByTournamentIdAndStatus(1L, RegistrationStatus.CONFIRMED))
                .thenReturn(makeTeamRegistrations(1));

        assertThatThrownBy(() -> bracketService.generateBracket(1L, BracketType.SINGLE_ELIMINATION))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ════════════════════════════════════════════════
    //  ROUND ROBIN — TEAM
    // ════════════════════════════════════════════════

    @Test
    void roundRobin_4teams_generates6Matches() {
        List<TournamentRegistration> regs = makeTeamRegistrations(4);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.ROUND_ROBIN);

        // 4 équipes → n*(n-1)/2 = 6 matchs
        assertThat(result).hasSize(6);
    }

    @Test
    void roundRobin_3teams_generates3Matches() {
        List<TournamentRegistration> regs = makeTeamRegistrations(3);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.ROUND_ROBIN);

        // 3 équipes (impair → +BYE) → 3 matchs réels
        assertThat(result).hasSize(3);
    }

    @Test
    void roundRobin_6teams_generates15Matches() {
        List<TournamentRegistration> regs = makeTeamRegistrations(6);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.ROUND_ROBIN);

        // 6*(6-1)/2 = 15
        assertThat(result).hasSize(15);
    }

    @Test
    void roundRobin_allMatchesHaveBracketTypeRoundRobin() {
        List<TournamentRegistration> regs = makeTeamRegistrations(4);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.ROUND_ROBIN);

        assertThat(result).allMatch(m -> m.getBracketType() == BracketType.ROUND_ROBIN);
    }

    @Test
    void roundRobin_noMatchHasSameTeamTwice() {
        List<TournamentRegistration> regs = makeTeamRegistrations(4);
        mockGenerate(teamTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(1L, BracketType.ROUND_ROBIN);

        // Aucun match ne doit avoir teamA == teamB
        result.stream()
                .filter(m -> m.getMatch() != null
                        && m.getMatch().getTeamA() != null
                        && m.getMatch().getTeamB() != null)
                .forEach(m -> assertThat(m.getMatch().getTeamA().getIdTeam())
                        .isNotEqualTo(m.getMatch().getTeamB().getIdTeam()));
    }

    // ════════════════════════════════════════════════
    //  INDIVIDUAL (PLAYER)
    // ════════════════════════════════════════════════

    @Test
    void singleElim_individual_4players_setsPlayer1AndPlayer2() {
        List<TournamentRegistration> regs = makePlayerRegistrations(4);
        mockGenerate(individualTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(2L, BracketType.SINGLE_ELIMINATION);

        List<TournamentMatch> r1 = result.stream().filter(m -> m.getRound() == 1).toList();
        assertThat(r1).allMatch(m -> m.getPlayer1() != null);
    }

    @Test
    void roundRobin_individual_4players_generates6Matches() {
        List<TournamentRegistration> regs = makePlayerRegistrations(4);
        mockGenerate(individualTournament, regs);

        List<TournamentMatch> result = bracketService.generateBracket(2L, BracketType.ROUND_ROBIN);

        assertThat(result).hasSize(6);
    }

    // ════════════════════════════════════════════════
    //  SUBMIT RESULT
    // ════════════════════════════════════════════════

    @Test
    void submitResult_alreadyCompleted_throws() {
        TournamentMatch tm = TournamentMatch.builder()
                .id(1L).status(TournamentMatchStatus.COMPLETED).build();
        when(tournamentMatchRepository.findById(1L)).thenReturn(Optional.of(tm));

        assertThatThrownBy(() -> bracketService.submitResult(1L, 1L, true))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void submitResult_invalidWinner_throws() {
        Team t1 = new Team(); t1.setIdTeam(1L);
        Team t2 = new Team(); t2.setIdTeam(2L);
        Match m = new Match(); m.setTeamA(t1); m.setTeamB(t2);

        TournamentMatch tm = TournamentMatch.builder()
                .id(1L).match(m).status(TournamentMatchStatus.PENDING).build();
        when(tournamentMatchRepository.findById(1L)).thenReturn(Optional.of(tm));

        // winnerId = 99 → pas dans ce match
        assertThatThrownBy(() -> bracketService.submitResult(1L, 99L, true))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
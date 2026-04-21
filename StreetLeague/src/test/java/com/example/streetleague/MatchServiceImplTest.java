package com.example.streetleague;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.MatchServiceImpl;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.MatchRequest;
import com.example.streetleague.dto.MatchResponse;
import com.example.streetleague.dto.MatchUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @InjectMocks private MatchServiceImpl matchService;
    @Mock private MatchRepository matchRepository;
    @Mock private TeamRepository  teamRepository;
    @Mock private UserRepository  userRepository;

    private User captain;
    private User captainB;
    private User admin;
    private Team teamA;
    private Team teamB;
    private Match match;

    @BeforeEach
    void setUp() {
        captain = new User();
        captain.setIdUser(10L);
        captain.setRole(Role.PLAYER);

        captainB = new User();
        captainB.setIdUser(20L);
        captainB.setRole(Role.PLAYER);

        admin = new User();
        admin.setIdUser(1L);
        admin.setRole(Role.ADMIN);

        teamA = new Team();
        teamA.setIdTeam(1L);
        teamA.setCaptain(captain);

        teamB = new Team();
        teamB.setIdTeam(2L);
        teamB.setCaptain(captainB);

        match = new Match();
        match.setIdMatch(1L);
        match.setStatus(MatchStatus.PENDING);
        match.setTeamA(teamA);
        match.setTeamB(teamB);
        match.setCreatedBy(captain);
        match.setLocation("Tunis");
    }

    @Test
    void addMatchTest() {
        MatchRequest dto = new MatchRequest(LocalDateTime.now().plusDays(1), "Tunis");
        when(teamRepository.findById(1L)).thenReturn(Optional.of(teamA));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(teamB));
        when(userRepository.findById(10L)).thenReturn(Optional.of(captain));
        when(matchRepository.findAll()).thenReturn(List.of());
        when(matchRepository.save(any())).thenReturn(match);

        MatchResponse res = matchService.addMatch(dto, 1L, 2L, 10L);

        assertNotNull(res);
        verify(matchRepository, times(1)).save(any());
    }

    @Test
    void updateMatchTest() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any())).thenReturn(match);

        MatchUpdateRequest dto = new MatchUpdateRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                "New Location",
                null,
                null,
                null
        );

        MatchResponse res = matchService.updateMatch(dto, 10L);

        assertNotNull(res);
        verify(matchRepository, times(1)).save(any());
    }

    @Test
    void deleteMatchTest() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        matchService.deleteMatch(1L, 1L);

        verify(matchRepository).deleteById(1L);
    }

    @Test
    void showMatchsTest() {
        when(matchRepository.findAll()).thenReturn(List.of(match));

        List<MatchResponse> result = matchService.ShowMatchs();

        assertEquals(1, result.size());
    }

    @Test
    void showMatchTest() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        MatchResponse result = matchService.ShowMatch(1L);

        assertNotNull(result);
    }
}
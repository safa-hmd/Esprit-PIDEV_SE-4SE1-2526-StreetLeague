package com.example.streetleague;

import com.example.streetleague.Entity.Level;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.TeamServiceImpl;
import com.example.streetleague.domain.Role;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TeamRequest;
import com.example.streetleague.dto.TeamResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @InjectMocks private TeamServiceImpl teamService;
    @Mock private TeamRepository  teamRepository;
    @Mock private UserRepository  userRepository;
    @Mock private MatchRepository matchRepository;

    private User captain;
    private User player;
    private User admin;
    private Team team;

//Initialise les objets avant chaque test
    @BeforeEach
    void setUp() {
        captain = new User();
        captain.setIdUser(1L);
        captain.setRole(Role.PLAYER);

        player = new User();
        player.setIdUser(2L);
        player.setRole(Role.PLAYER);

        admin = new User();
        admin.setIdUser(3L);
        admin.setRole(Role.ADMIN);

        team = new Team();
        team.setIdTeam(1L);
        team.setName("TeamX");
        team.setSport("Football");
        team.setLevel(Level.BEGINNER);
        team.setCaptain(captain);
        team.setPlayers(new ArrayList<>());
    }

    @Test
    void addTeamTest() {
        TeamRequest dto = new TeamRequest("TeamX", "Football", "desc", Level.BEGINNER);
        //Arrange (préparer)
        when(userRepository.findById(1L)).thenReturn(Optional.of(captain));
        when(teamRepository.findAll()).thenReturn(List.of());
        when(teamRepository.save(any())).thenReturn(team);

        //Act (exécuter)
        TeamResponse res = teamService.addTeam(dto, 1L);

        //Assert (vérifier)
        assertNotNull(res);
        assertEquals("TeamX", res.name());
        verify(teamRepository, times(1)).save(any());
    }

    @Test
    void updateTeamTest() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.findAll()).thenReturn(List.of(team));
        when(teamRepository.save(any())).thenReturn(team);

        TeamResponse res = teamService.updateTeam(1L,
                new TeamRequest("NewName", "Football", "desc", Level.ADVANCED), 1L);

        assertNotNull(res);
        verify(teamRepository, times(1)).save(any());
    }

    @Test
    void deleteTeamTest() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(3L)).thenReturn(Optional.of(admin));
        doNothing().when(matchRepository).deleteByTeamAIdOrTeamBId(1L);
        when(teamRepository.save(any())).thenReturn(team);

        teamService.deleteTeam(1L, 3L);

        verify(matchRepository).deleteByTeamAIdOrTeamBId(1L);
        verify(teamRepository).deleteById(1L);
    }

    @Test
    void showTeamsTest() {
        when(teamRepository.findAll()).thenReturn(List.of(team));

        List<TeamResponse> result = teamService.ShowTeams();

        assertEquals(1, result.size());
        assertEquals("TeamX", result.get(0).name());
    }

    @Test
    void showTeamTest() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        TeamResponse result = teamService.ShowTeam(1L);

        assertNotNull(result);
        assertEquals("TeamX", result.name());
    }

    @Test
    void joinTeamTest() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(2L)).thenReturn(Optional.of(player));
        when(teamRepository.save(any())).thenReturn(team);

        TeamResponse res = teamService.joinTeam(1L, 2L);

        assertNotNull(res);
        assertTrue(team.getPlayers().contains(player));
    }

    @Test
    void leaveTeamTest() {
        team.getPlayers().add(player);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(userRepository.findById(2L)).thenReturn(Optional.of(player));
        when(teamRepository.save(any())).thenReturn(team);

        TeamResponse res = teamService.leaveTeam(1L, 2L);

        assertNotNull(res);
        assertFalse(team.getPlayers().contains(player));
    }

    @Test
    void getTeamsByCaptainTest() {
        when(teamRepository.findAll()).thenReturn(List.of(team));

        List<TeamResponse> result = teamService.getTeamsByCaptain(1L);

        assertEquals(1, result.size());
    }
}
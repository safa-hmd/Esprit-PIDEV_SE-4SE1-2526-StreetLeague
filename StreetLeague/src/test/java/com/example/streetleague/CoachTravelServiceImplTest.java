package com.example.streetleague;

import com.example.streetleague.Entity.*;
import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.CoachTravelServiceImpl;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.TransportEligibilityResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoachTravelServiceImplTest {

    @InjectMocks
    private CoachTravelServiceImpl coachTravelService;

    @Mock private TravelRequestRepository travelRequestRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private TournamentRepository tournamentRepository;
    @Mock private TransportRepository transportRepository;
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private AccommodationRequestRepository accommodationRequestRepository;
    @Mock private UserRepository userRepository;

    private User coach;
    private Team team;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        coach = new User();
        coach.setIdUser(1L);

        team = new Team();
        team.setIdTeam(1L);
        team.setCity("Tunis");

        tournament = new Tournament();
        tournament.setId(100L);
        tournament.setCity("Sousse");
    }

    @Test
    void getAvailableTransports_ReturnsAll() {
        Transport t = new Transport();
        when(transportRepository.findAll()).thenReturn(List.of(t));
        
        List<Transport> result = coachTravelService.getAvailableTransports();
        
        assertEquals(1, result.size());
    }

    @Test
    void checkEligibility_DifferentCity_ReturnsEligible() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(coach));
        when(teamRepository.findByCaptain_IdUser(1L)).thenReturn(List.of(team));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(transportRepository.findAll()).thenReturn(Collections.emptyList());

        TransportEligibilityResponseDto res = coachTravelService.checkEligibilityAndGetTransports(1L, 100L);

        assertTrue(res.isEligible());
    }

    @Test
    void checkEligibility_SameCity_ReturnsNotEligible() {
        tournament.setCity("Tunis");
        when(userRepository.findById(1L)).thenReturn(Optional.of(coach));
        when(teamRepository.findByCaptain_IdUser(1L)).thenReturn(List.of(team));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));

        TransportEligibilityResponseDto res = coachTravelService.checkEligibilityAndGetTransports(1L, 100L);

        assertFalse(res.isEligible());
        assertEquals("Team and Tournament are in the same city. No transport required.", res.getReason());
    }

    @Test
    void resolveTeamId_ReturnsFoundIdFromCaptained() {
        // Force findTeamIdByUserId to return null to test captained fallback
        when(userRepository.findTeamIdByUserId(1L)).thenReturn(null);
        when(teamRepository.findByCaptain_IdUser(1L)).thenReturn(List.of(team));
        
        Long id = coachTravelService.resolveTeamId(1L);
        
        assertEquals(1L, id);
    }
    
    @Test
    void resolveTeamId_ReturnsFoundIdFromJunction() {
        when(userRepository.findTeamIdByUserId(1L)).thenReturn(1L);
        
        Long id = coachTravelService.resolveTeamId(1L);
        
        assertEquals(1L, id);
    }
}

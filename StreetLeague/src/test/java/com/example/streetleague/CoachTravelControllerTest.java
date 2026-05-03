package com.example.streetleague;

import com.example.streetleague.Controller.CoachTravelController;
import com.example.streetleague.Entity.Team;
import com.example.streetleague.Repository.TeamRepository;
import com.example.streetleague.Repository.UserRepository;
import com.example.streetleague.ServiceInterface.CoachTravelService;
import com.example.streetleague.exception.BusinessValidationException;
import com.example.streetleague.security.CustomUserDetailsService;
import com.example.streetleague.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoachTravelController.class)
@AutoConfigureMockMvc(addFilters = false)
class CoachTravelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CoachTravelService coachTravelService;
    @MockBean private UserRepository userRepository;
    @MockBean private TeamRepository teamRepository;
    @MockBean private JwtService jwtService;
    @MockBean private CustomUserDetailsService userDetailsService;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void getMyTeam_ReturnsTeamInfo() throws Exception {
        Team team = new Team();
        team.setIdTeam(1L);
        team.setName("Street Kings");
        team.setCity("Tunis");

        when(userRepository.findTeamIdByUserId(1L)).thenReturn(1L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        mockMvc.perform(get("/api/coach/travel/my-team?coachId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Street Kings"))
                .andExpect(jsonPath("$.city").value("Tunis"));
    }

    @Test
    void getMyTeam_NoTeam_ReturnsEmptyMap() throws Exception {
        when(userRepository.findTeamIdByUserId(1L)).thenReturn(null);

        mockMvc.perform(get("/api/coach/travel/my-team?coachId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAvailableTransports_ReturnsList() throws Exception {
        when(coachTravelService.getAvailableTransports()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/coach/travel/transports/tournament/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

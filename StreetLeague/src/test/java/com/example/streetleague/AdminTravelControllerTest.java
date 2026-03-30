package com.example.streetleague;

import com.example.streetleague.Controller.AdminTravelController;
import com.example.streetleague.Entity.Accommodation;
import com.example.streetleague.Entity.AccommodationType;
import com.example.streetleague.Entity.Transport;
import com.example.streetleague.Entity.TransportType;
import com.example.streetleague.ServiceInterface.AdminTravelService;
import com.example.streetleague.dto.AccommodationDto;
import com.example.streetleague.dto.TransportDto;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminTravelController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminTravelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminTravelService adminTravelService;

    // Fixed: Mocking security-related beans required by JwtAuthFilter component
    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addTransport_ReturnsCreatedTransport() throws Exception {
        TransportDto dto = TransportDto.builder()
                .type(TransportType.BUS)
                .destination("Sousse")
                .build();

        Transport transport = Transport.builder()
                .id(1L)
                .type(TransportType.BUS)
                .build();

        when(adminTravelService.addTransport(any(TransportDto.class))).thenReturn(transport);

        mockMvc.perform(post("/api/admin/travel/transport")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("BUS"));
    }

    @Test
    void getAllTransports_ReturnsList() throws Exception {
        TransportDto dto = TransportDto.builder().id(1L).type(TransportType.BUS).build();
        when(adminTravelService.getAllTransports()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/travel/transport"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("BUS"));
    }

    @Test
    void addAccommodation_ReturnsCreatedAccommodation() throws Exception {
        AccommodationDto dto = AccommodationDto.builder()
                .type(AccommodationType.HOTEL)
                .address("Tunis")
                .build();

        Accommodation acc = Accommodation.builder()
                .id(1L)
                .type(AccommodationType.HOTEL)
                .build();

        when(adminTravelService.addAccommodation(any(AccommodationDto.class))).thenReturn(acc);

        mockMvc.perform(post("/api/admin/travel/accommodation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("HOTEL"));
    }
}

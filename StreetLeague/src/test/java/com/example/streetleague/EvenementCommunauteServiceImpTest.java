package com.example.streetleague;

import com.example.streetleague.Repository.CommunauteRepository;
import com.example.streetleague.Repository.EvenementCommunauteRepository;
import com.example.streetleague.Repository.SponsoringEvenementRepository;
import com.example.streetleague.ServiceImp.EvenementCommunauteServiceImp;
import com.example.streetleague.domain.Communaute;
import com.example.streetleague.domain.SponsoringEvenement;
import com.example.streetleague.dto.EvenementCommunauteDTO;
import com.example.streetleague.mapper.EvenementCommunauteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvenementCommunauteServiceImpTest {

    @Mock
    private EvenementCommunauteRepository repo;
    @Mock
    private EvenementCommunauteMapper mapper;
    @Mock
    private CommunauteRepository communauteRepository;
    @Mock
    private SponsoringEvenementRepository sponsoringEvenementRepository;
    @InjectMocks
    private EvenementCommunauteServiceImp service;

    private EvenementCommunauteDTO dto;

    @BeforeEach
    void setUp() {
        dto = new EvenementCommunauteDTO(1L, "Event", "Desc", new Date(), 10L, 20L);
    }

    @Test
    void create_shouldThrowWhenCommunauteMissing() {
        when(communauteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.create(dto));
    }

    @Test
    void update_shouldThrowWhenCommunauteMissing() {
        com.example.streetleague.domain.EvenementCommunaute event = new com.example.streetleague.domain.EvenementCommunaute();
        when(repo.findById(1L)).thenReturn(Optional.of(event));
        when(communauteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(1L, dto));
    }

    @Test
    void delete_shouldDeleteSponsoringsAndEvent() {
        List<SponsoringEvenement> sponsorings = new ArrayList<>();
        sponsorings.add(new SponsoringEvenement());
        when(sponsoringEvenementRepository.findByEvenementId(1L)).thenReturn(sponsorings);

        service.delete(1L);

        verify(sponsoringEvenementRepository).deleteAll(sponsorings);
        verify(repo).deleteById(1L);
    }
}

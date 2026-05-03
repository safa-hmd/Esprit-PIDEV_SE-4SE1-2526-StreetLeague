package com.example.streetleague;

import com.example.streetleague.Repository.EvenementCommunauteRepository;
import com.example.streetleague.Repository.SponsorRepository;
import com.example.streetleague.Repository.SponsoringEvenementRepository;
import com.example.streetleague.ServiceImp.SponsoringEvenementServiceImp;
import com.example.streetleague.domain.EvenementCommunaute;
import com.example.streetleague.domain.Sponsor;
import com.example.streetleague.domain.SponsoringEvenement;
import com.example.streetleague.dto.SponsoringEvenementDTO;
import com.example.streetleague.mapper.SponsoringEvenementMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SponsoringEvenementServiceImpTest {

    @Mock
    private SponsoringEvenementRepository repo;
    @Mock
    private SponsoringEvenementMapper mapper;
    @Mock
    private SponsorRepository sponsorRepository;
    @Mock
    private EvenementCommunauteRepository evenementRepository;
    @InjectMocks
    private SponsoringEvenementServiceImp service;

    private SponsoringEvenement entity;

    @BeforeEach
    void setUp() {
        entity = new SponsoringEvenement();
        entity.setId(1L);
    }

    @Test
    void create_shouldThrowWhenSponsorMissing() {
        SponsoringEvenementDTO dto = new SponsoringEvenementDTO(null, 77L, 88L, BigDecimal.TEN, "Cash");
        when(sponsorRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.create(dto));
    }

    @Test
    void create_shouldThrowWhenEventMissing() {
        SponsoringEvenementDTO dto = new SponsoringEvenementDTO(null, 1L, 88L, BigDecimal.TEN, "Cash");
        Sponsor sponsor = new Sponsor();
        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));
        when(evenementRepository.findById(88L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.create(dto));
    }

    @Test
    void delete_shouldDeleteEntity() {
        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repo).delete(entity);
    }
}

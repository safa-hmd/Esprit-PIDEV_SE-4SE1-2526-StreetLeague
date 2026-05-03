package com.example.streetleague;

import com.example.streetleague.mapper.ContratSponsorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratSponsorServiceImpTest {

    @Mock
    private ContratSponsorRepository repo;
    @Mock
    private ContratSponsorMapper mapper;
    @Mock
    private SponsorRepository sponsorRepository;
    @InjectMocks
    private ContratSponsorServiceImp service;

    private Sponsor sponsor;

    @BeforeEach
    void setUp() {
        sponsor = new Sponsor();
        sponsor.setId(1L);
    }

    @Test
    void create_shouldThrowWhenSponsorMissing() {
        Date now = new Date();
        ContratSponsorDTO dto = new ContratSponsorDTO(null, 50L, 20L, BigDecimal.TEN, now, now, "ACTIF", "");
        when(sponsorRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.create(dto));
    }

    @Test
    void create_shouldThrowWhenDateFinBeforeDateDebut() {
        Date debut = new Date(2000L);
        Date fin = new Date(1000L);
        ContratSponsorDTO dto = new ContratSponsorDTO(null, 1L, 20L, BigDecimal.TEN, debut, fin, "ACTIF", "");
        when(sponsorRepository.findById(1L)).thenReturn(Optional.of(sponsor));

        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(3L);

        verify(repo).deleteById(3L);
    }
}

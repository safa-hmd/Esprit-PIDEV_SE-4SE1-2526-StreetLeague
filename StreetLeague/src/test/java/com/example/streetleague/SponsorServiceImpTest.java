package com.example.streetleague;

import com.example.streetleague.mapper.SponsorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SponsorServiceImpTest {

    @Mock
    private SponsorRepository repo;
    @Mock
    private SponsorMapper mapper;
    @Mock
    private SponsoringEvenementRepository sponsoringEvenementRepository;
    @Mock
    private ContratSponsorRepository contratSponsorRepository;
    @InjectMocks
    private SponsorServiceImp service;

    private Sponsor sponsor;

    @BeforeEach
    void setUp() {
        sponsor = new Sponsor();
        sponsor.setId(1L);
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.getById(999L));
    }

    @Test
    void update_shouldKeepExistingId() {
        SponsorDTO dto = new SponsorDTO(null, "N", "T", "a@b.com", "12345678", "Adr");
        Sponsor mapped = new Sponsor();
        Sponsor saved = new Sponsor();
        saved.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(sponsor));
        when(mapper.toEntity(dto)).thenReturn(mapped);
        when(repo.save(mapped)).thenReturn(saved);
        when(mapper.toDTO(saved)).thenReturn(new SponsorDTO(1L, "N", "T", "a@b.com", "12345678", "Adr"));

        service.update(1L, dto);

        verify(repo).save(mapped);
    }

    @Test
    void delete_shouldDeleteRelationsThenSponsor() {
        when(repo.findById(1L)).thenReturn(Optional.of(sponsor));

        service.delete(1L);

        verify(sponsoringEvenementRepository).deleteAllForSponsor(1L);
        verify(contratSponsorRepository).deleteAllForSponsor(1L);
        verify(repo).delete(sponsor);
    }
}

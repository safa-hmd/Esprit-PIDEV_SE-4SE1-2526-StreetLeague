package com.example.streetleague;

import com.example.streetleague.Repository.TransporteurRepository;
import com.example.streetleague.ServiceImp.TransporteurServiceImp;
import com.example.streetleague.domain.Transporteur;
import com.example.streetleague.dto.TransporteurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransporteurServiceImpTest {

    @InjectMocks private TransporteurServiceImp transporteurService;
    @Mock private TransporteurRepository transporteurRepository;

    private Transporteur transporteur;
    private TransporteurDTO transporteurDTO;

    @BeforeEach
    void setUp() {
        transporteur = new Transporteur();
        transporteur.setId(1L);
        transporteur.setNomSociete("Tunisie Express");
        transporteur.setTelephone("+216 71 000 000");
        transporteur.setEmail("contact@tunisieexpress.tn");

        transporteurDTO = TransporteurDTO.builder()
                .nomSociete("Tunisie Express")
                .telephone("+216 71 000 000")
                .email("contact@tunisieexpress.tn")
                .build();
    }

    // ── create ────────────────────────────────────────────

    @Test
    void createTransporteurTest() {
        when(transporteurRepository.save(any())).thenReturn(transporteur);

        Transporteur result = transporteurService.createTransporteur(transporteurDTO);

        assertNotNull(result);
        assertEquals("Tunisie Express", result.getNomSociete());
        assertEquals("+216 71 000 000", result.getTelephone());
        verify(transporteurRepository, times(1)).save(any());
    }

    // ── update ────────────────────────────────────────────

    @Test
    void updateTransporteurTest() {
        when(transporteurRepository.findById(1L)).thenReturn(Optional.of(transporteur));
        when(transporteurRepository.save(any())).thenReturn(transporteur);

        TransporteurDTO updatedDTO = TransporteurDTO.builder()
                .nomSociete("Rapid Livraison")
                .telephone("+216 99 999 999")
                .email("info@rapid.tn")
                .build();

        Transporteur result = transporteurService.updateTransporteur(1L, updatedDTO);

        assertNotNull(result);
        verify(transporteurRepository, times(1)).save(any());
    }

    @Test
    void updateTransporteur_notFound_throwsException() {
        when(transporteurRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transporteurService.updateTransporteur(99L, transporteurDTO));

        assertEquals("Transporteur introuvable", ex.getMessage());
        verify(transporteurRepository, never()).save(any());
    }

    // ── delete ────────────────────────────────────────────

    @Test
    void deleteTransporteurTest() {
        doNothing().when(transporteurRepository).deleteById(1L);

        transporteurService.deleteTransporteur(1L);

        verify(transporteurRepository, times(1)).deleteById(1L);
    }

    // ── getById ───────────────────────────────────────────

    @Test
    void getTransporteurByIdTest() {
        when(transporteurRepository.findById(1L)).thenReturn(Optional.of(transporteur));

        Transporteur result = transporteurService.getTransporteurById(1L);

        assertNotNull(result);
        assertEquals("Tunisie Express", result.getNomSociete());
    }

    @Test
    void getTransporteurById_notFound_throwsException() {
        when(transporteurRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transporteurService.getTransporteurById(99L));

        assertEquals("Transporteur introuvable", ex.getMessage());
    }

    // ── getAll ────────────────────────────────────────────

    @Test
    void getAllTransporteursTest() {
        when(transporteurRepository.findAll()).thenReturn(List.of(transporteur));

        List<Transporteur> result = transporteurService.getAllTransporteurs();

        assertEquals(1, result.size());
        assertEquals("Tunisie Express", result.get(0).getNomSociete());
    }

    @Test
    void getAllTransporteurs_emptyList() {
        when(transporteurRepository.findAll()).thenReturn(List.of());

        List<Transporteur> result = transporteurService.getAllTransporteurs();

        assertTrue(result.isEmpty());
    }
}
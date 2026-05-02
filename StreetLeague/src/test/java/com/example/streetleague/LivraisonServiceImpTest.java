package com.example.streetleague;

import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.LivraisonServiceImp;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.LivraisonDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivraisonServiceImpTest {

    @InjectMocks private LivraisonServiceImp livraisonService;
    @Mock private LivraisonRepository  livraisonRepository;
    @Mock private CommandeRepository   commandeRepository;
    @Mock private TransporteurRepository transporteurRepository;
    @Mock private UserRepository       userRepository;

    private Commande    commande;
    private Transporteur transporteur;
    private User         livreur;
    private Livraison    livraison;
    private LivraisonDTO livraisonDTO;

    @BeforeEach
    void setUp() {
        commande = new Commande();
        commande.setId(1L);
        commande.setStatut(CommandeStatus.PREPAREE);

        transporteur = new Transporteur();
        transporteur.setId(1L);
        transporteur.setNomSociete("Tunisie Express");

        livreur = new User();
        livreur.setId(5L);

        livraison = new Livraison();
        livraison.setId(1L);
        livraison.setCommande(commande);
        livraison.setTransporteur(transporteur);
        livraison.setAdresse("12 Rue de la Paix, Tunis");
        livraison.setFraisLivraison(7.5);
        livraison.setStatut(LivraisonStatus.PREPAREE);

        livraisonDTO = LivraisonDTO.builder()
                .commandeId(1L)
                .transporteurId(1L)
                .adresse("12 Rue de la Paix, Tunis")
                .fraisLivraison(7.5)
                .statut(LivraisonStatus.PREPAREE)
                .build();
    }

    // ── createLivraison ───────────────────────────────────

    @Test
    void createLivraisonTest() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(transporteurRepository.findById(1L)).thenReturn(Optional.of(transporteur));
        when(livraisonRepository.save(any())).thenReturn(livraison);

        Livraison result = livraisonService.createLivraison(livraisonDTO);

        assertNotNull(result);
        assertEquals(LivraisonStatus.PREPAREE, result.getStatut());
        assertEquals("12 Rue de la Paix, Tunis", result.getAdresse());
        verify(livraisonRepository, times(1)).save(any());
    }

    @Test
    void createLivraison_withLivreur() {
        livraisonDTO.setLivreurId(5L);
        livraison.setLivreur(livreur);

        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(transporteurRepository.findById(1L)).thenReturn(Optional.of(transporteur));
        when(userRepository.findById(5L)).thenReturn(Optional.of(livreur));
        when(livraisonRepository.save(any())).thenReturn(livraison);

        Livraison result = livraisonService.createLivraison(livraisonDTO);

        assertNotNull(result);
        assertNotNull(result.getLivreur());
        verify(userRepository, times(1)).findById(5L);
    }

    @Test
    void createLivraison_commandeNotFound_throwsException() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> livraisonService.createLivraison(livraisonDTO));

        assertEquals("Commande introuvable", ex.getMessage());
        verify(livraisonRepository, never()).save(any());
    }

    @Test
    void createLivraison_transporteurNotFound_throwsException() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(transporteurRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> livraisonService.createLivraison(livraisonDTO));

        assertEquals("Transporteur introuvable", ex.getMessage());
        verify(livraisonRepository, never()).save(any());
    }

    @Test
    void createLivraison_livreurNotFound_throwsException() {
        livraisonDTO.setLivreurId(99L);
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(transporteurRepository.findById(1L)).thenReturn(Optional.of(transporteur));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> livraisonService.createLivraison(livraisonDTO));

        assertEquals("Livreur introuvable", ex.getMessage());
    }

    // ── updateLivraisonStatus ─────────────────────────────

    @Test
    void updateLivraisonStatusTest() {
        when(livraisonRepository.findById(1L)).thenReturn(Optional.of(livraison));
        when(livraisonRepository.save(any())).thenReturn(livraison);

        LivraisonDTO updateDTO = LivraisonDTO.builder()
                .commandeId(1L)
                .transporteurId(1L)
                .adresse("12 Rue de la Paix, Tunis")
                .fraisLivraison(7.5)
                .statut(LivraisonStatus.LIVREE)
                .build();

        Livraison result = livraisonService.updateLivraisonStatus(1L, updateDTO);

        assertNotNull(result);
        verify(livraisonRepository, times(1)).save(any());
    }

    @Test
    void updateLivraisonStatus_notFound_throwsException() {
        when(livraisonRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> livraisonService.updateLivraisonStatus(99L, livraisonDTO));

        assertEquals("Livraison introuvable", ex.getMessage());
    }

    // ── getLivraisonById ──────────────────────────────────

    @Test
    void getLivraisonByIdTest() {
        when(livraisonRepository.findById(1L)).thenReturn(Optional.of(livraison));

        Livraison result = livraisonService.getLivraisonById(1L);

        assertNotNull(result);
        assertEquals(7.5, result.getFraisLivraison());
    }

    @Test
    void getLivraisonById_notFound_throwsException() {
        when(livraisonRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> livraisonService.getLivraisonById(99L));

        assertEquals("Livraison introuvable", ex.getMessage());
    }

    // ── getAllLivraisons ───────────────────────────────────

    @Test
    void getAllLivraisonsTest() {
        when(livraisonRepository.findAll()).thenReturn(List.of(livraison));

        List<Livraison> result = livraisonService.getAllLivraisons();

        assertEquals(1, result.size());
        assertEquals(LivraisonStatus.PREPAREE, result.get(0).getStatut());
    }

    @Test
    void getAllLivraisons_emptyList() {
        when(livraisonRepository.findAll()).thenReturn(List.of());

        List<Livraison> result = livraisonService.getAllLivraisons();

        assertTrue(result.isEmpty());
    }
}
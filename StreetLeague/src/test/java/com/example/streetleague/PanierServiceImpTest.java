package com.example.streetleague;

import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.PanierServiceImp;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PanierServiceImpTest {

    @InjectMocks private PanierServiceImp panierService;
    @Mock private PanierRepository       panierRepository;
    @Mock private LignePanierRepository  lignePanierRepository;
    @Mock private UserRepository         userRepository;
    @Mock private MaterielRepository     materielRepository;

    private User        user;
    private Materiel    materiel;
    private Panier      panier;
    private LignePanier lignePanier;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setRole(Role.PLAYER);

        materiel = new Materiel();
        materiel.setId(1L);
        materiel.setNom("Ballon Pro");
        materiel.setPrix(49.90);
        materiel.setQuantiteStock(10);

        lignePanier = new LignePanier();
        lignePanier.setId(1L);
        lignePanier.setMateriel(materiel);
        lignePanier.setQuantite(2);

        panier = new Panier();
        panier.setId(1L);
        panier.setUser(user);
        panier.setLignes(new ArrayList<>(List.of(lignePanier)));
    }

    // ── addToCart ─────────────────────────────────────────

    @Test
    void addToCart_newItem_createsLigne() {
        panier.setLignes(new ArrayList<>()); // panier vide

        AddToCartDTO dto = AddToCartDTO.builder()
                .userId(1L)
                .materielId(1L)
                .quantite(1)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(materielRepository.findById(1L)).thenReturn(Optional.of(materiel));
        when(panierRepository.findByUserId(1L)).thenReturn(Optional.of(panier));
        when(panierRepository.save(any())).thenReturn(panier);

        panierService.addToCart(dto);

        verify(lignePanierRepository, times(1)).save(any());
        verify(panierRepository, times(1)).save(panier);
    }

    @Test
    void addToCart_existingItem_incrementsQuantite() {
        // Panier contient déjà le materiel (id=1) avec quantite=2
        AddToCartDTO dto = AddToCartDTO.builder()
                .userId(1L)
                .materielId(1L)
                .quantite(3)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(materielRepository.findById(1L)).thenReturn(Optional.of(materiel));
        when(panierRepository.findByUserId((1L)).iterator();
        when(panierRepository.save(any())).thenReturn(panier);

        panierService.addToCart(dto);

        // Quantite doit être 2 + 3 = 5
        assertEquals(5, lignePanier.getQuantite());
        verify(lignePanierRepository, never()).save(any()); // pas de nouvelle ligne
        verify(panierRepository, times(1)).save(panier);
    }

    @Test
    void addToCart_createsPanierIfNotExists() {
        AddToCartDTO dto = AddToCartDTO.builder()
                .userId(1L)
                .materielId(1L)
                .quantite(1)
                .build();

        Panier newPanier = Panier.builder()
                .user(user)
                .lignes(new ArrayList<>())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(materielRepository.findById(1L)).thenReturn(Optional.of(materiel));
        when(panierRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(panierRepository.save(any())).thenReturn(newPanier);

        panierService.addToCart(dto);

        // save appelé 2 fois : création panier + sauvegarde finale
        verify(panierRepository, times(2)).save(any());
    }

    @Test
    void addToCart_userNotFound_throwsException() {
        AddToCartDTO dto = AddToCartDTO.builder()
                .userId(99L).materielId(1L).quantite(1).build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> panierService.addToCart(dto));

        assertEquals("Utilisateur introuvable", ex.getMessage());
    }

    @Test
    void addToCart_materielNotFound_throwsException() {
        AddToCartDTO dto = AddToCartDTO.builder()
                .userId(1L).materielId(99L).quantite(1).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(materielRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> panierService.addToCart(dto));

        assertEquals("Materiel introuvable", ex.getMessage());
    }

    // ── getUserCart ───────────────────────────────────────

    @Test
    void getUserCartTest() {
        when(panierRepository.findByUserId(1L)).thenReturn(Optional.of(panier));

        PanierResponseDTO result = panierService.getUserCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPanierId());
        assertEquals(1L, result.getUserId());
        assertEquals(1, result.getLignes().size());
        // total = 49.90 * 2 = 99.80
        assertEquals(99.80, result.getTotal(), 0.01);
    }

    @Test
    void getUserCart_panierNotFound_throwsException() {
        when(panierRepository.findByUserId(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> panierService.getUserCart(99L));

        assertEquals("Panier vide", ex.getMessage());
    }

    // ── updateQuantity ────────────────────────────────────

    @Test
    void updateQuantityTest() {
        UpdateCartDTO dto = UpdateCartDTO.builder()
                .lignePanierId(1L)
                .quantite(5)
                .build();

        when(lignePanierRepository.findById(1L)).thenReturn(Optional.of(lignePanier));

        panierService.updateQuantity(dto);

        assertEquals(5, lignePanier.getQuantite());
    }

    @Test
    void updateQuantity_ligneNotFound_throwsException() {
        UpdateCartDTO dto = UpdateCartDTO.builder()
                .lignePanierId(99L)
                .quantite(5)
                .build();

        when(lignePanierRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> panierService.updateQuantity(dto));

        assertEquals("Ligne introuvable", ex.getMessage());
    }

    // ── removeItem ────────────────────────────────────────

    @Test
    void removeItemTest() {
        doNothing().when(lignePanierRepository).deleteById(1L);

        panierService.removeItem(1L);

        verify(lignePanierRepository, times(1)).deleteById(1L);
    }

    // ── clearCart ─────────────────────────────────────────

    @Test
    void clearCartTest() {
        when(panierRepository.findByUserId(1L)).thenReturn(Optional.of(panier));
        doNothing().when(lignePanierRepository).deleteAll(any());

        panierService.clearCart(1L);

        verify(lignePanierRepository, times(1)).deleteAll(panier.getLignes());
        assertTrue(panier.getLignes().isEmpty());
    }

    @Test
    void clearCart_panierNotFound_throwsException() {
        when(panierRepository.findByUserId(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> panierService.clearCart(99L));

        assertEquals("Panier introuvable", ex.getMessage());
    }
}
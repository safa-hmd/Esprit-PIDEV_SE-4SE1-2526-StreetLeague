package com.example.streetleague;

import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceImp.CommandeServiceImp;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.CommandeDTO;
import com.example.streetleague.dto.LigneCommandeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceImpTest {

    @InjectMocks
    private CommandeServiceImp commandeService;

    @Mock private CommandeRepository commandeRepository;
    @Mock private UserRepository userRepository;
    @Mock private MaterielRepository materielRepository;
    @Mock private PanierRepository panierRepository;
    @Mock private LignePanierRepository lignePanierRepository;

    private User user;
    private Materiel materiel;
    private Commande commande;
    private Panier panier;
    private LignePanier lignePanier;

    @BeforeEach
    void setUp() {
        // --- USER ---
        user = new User();
        user.setId(1L);
        user.setRole(Role.PLAYER);

        // --- MATERIEL ---
        materiel = new Materiel();
        materiel.setId(1L);
        materiel.setNom("Ballon Pro");
        materiel.setPrix(49.90);
        materiel.setQuantiteStock(10);

        // --- COMMANDE ---
        commande = new Commande();
        commande.setId(1L);
        commande.setUser(user);
        commande.setStatut(CommandeStatus.PREPAREE);
        commande.setMontantTotal(49.90);
        commande.setLignes(new ArrayList<>());

        // --- LIGNE PANIER ---
        lignePanier = new LignePanier();
        lignePanier.setId(1L);
        lignePanier.setMateriel(materiel);
        lignePanier.setQuantite(1);

        // --- PANIER ---
        panier = new Panier();
        panier.setId(1L);
        panier.setUser(user);
        panier.setLignes(new ArrayList<>(List.of(lignePanier)));
    }

    // ── TEST CREATE COMMANDE ─────────────────────────────
    @Test
    void createCommandeTest() {
        LigneCommandeDTO ligneDTO = new LigneCommandeDTO();
        ligneDTO.setMaterielId(1L);
        ligneDTO.setQuantite(1);

        CommandeDTO dto = new CommandeDTO();
        dto.setUserId(1L);
        dto.setStatut("PREPAREE");
        dto.setLignes(List.of(ligneDTO));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(materielRepository.findById(1L)).thenReturn(Optional.of(materiel));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);

        Commande result = commandeService.createCommande(dto);

        assertNotNull(result);
        assertEquals(CommandeStatus.PREPAREE, result.getStatut());
        verify(commandeRepository, times(1)).save(any());
    }

    @Test
    void createCommande_userNotFound_throwsException() {
        CommandeDTO dto = new CommandeDTO();
        dto.setUserId(99L);
        dto.setStatut("PREPAREE");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commandeService.createCommande(dto));

        assertEquals("Utilisateur introuvable", ex.getMessage());
    }

    @Test
    void createCommande_materielNotFound_throwsException() {
        LigneCommandeDTO ligneDTO = new LigneCommandeDTO();
        ligneDTO.setMaterielId(99L);
        ligneDTO.setQuantite(1);

        CommandeDTO dto = new CommandeDTO();
        dto.setUserId(1L);
        dto.setStatut("PREPAREE");
        dto.setLignes(List.of(ligneDTO));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(materielRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commandeService.createCommande(dto));

        assertEquals("Materiel introuvable", ex.getMessage());
    }

    // ── TEST CHECKOUT ───────────────────────────────────
    @Test
    void checkoutTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(panierRepository.findByUserId(1L)).thenReturn(Optional.of(panier));
        when(materielRepository.save(any(Materiel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(invocation -> {
            Commande c = invocation.getArgument(0);
            c.setId(1L); // simuler l'ID généré par la BDD
            return c;
        });
        doNothing().when(lignePanierRepository).deleteAll(any());

        Long commandeId = commandeService.checkout(1L);

        assertNotNull(commandeId);
        verify(commandeRepository, times(1)).save(any());
        verify(lignePanierRepository, times(1)).deleteAll(any());
        verify(materielRepository, times(1)).save(any());
    }
}
package com.example.streetleague;

import com.example.streetleague.Repository.CategoryRepository;
import com.example.streetleague.Repository.MaterielRepository;
import com.example.streetleague.ServiceImp.MaterielServiceImp;
import com.example.streetleague.domain.Category;
import com.example.streetleague.domain.Materiel;
import com.example.streetleague.dto.MaterielDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterielServiceImpTest {

    @InjectMocks private MaterielServiceImp materielService;
    @Mock private MaterielRepository materielRepository;
    @Mock private CategoryRepository categoryRepository;

    private Category category;
    private Materiel materiel;
    private MaterielDTO materielDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setNom("Football");

        materiel = new Materiel();
        materiel.setId(1L);
        materiel.setNom("Ballon Pro");
        materiel.setDescription("Ballon de football professionnel");
        materiel.setPrix(49.90);
        materiel.setQuantiteStock(20);
        materiel.setImageUrl("http://img.com/ballon.jpg");
        materiel.setCategorie(category);

        materielDTO = MaterielDTO.builder()
                .id(1L)
                .nom("Ballon Pro")
                .description("Ballon de football professionnel")
                .prix(49.90)
                .quantiteStock(20)
                .imageUrl("http://img.com/ballon.jpg")
                .categorieId(1L)
                .categorieNom("Football")
                .build();
    }

    // ── create ────────────────────────────────────────────

    @Test
    void createMaterielTest() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(materielRepository.save(any())).thenReturn(materiel);

        MaterielDTO result = materielService.create(materielDTO);

        assertNotNull(result);
        assertEquals("Ballon Pro", result.getNom());
        assertEquals(49.90, result.getPrix());
        verify(materielRepository, times(1)).save(any());
    }

    @Test
    void createMateriel_categoryNotFound_throwsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> materielService.create(materielDTO));

        assertEquals("Categorie introuvable", ex.getMessage());
        verify(materielRepository, never()).save(any());
    }

    // ── update ────────────────────────────────────────────

    @Test
    void updateMaterielTest() {
        when(materielRepository.findById(1L)).thenReturn(Optional.of(materiel));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(materielRepository.save(any())).thenReturn(materiel);

        MaterielDTO updatedDTO = MaterielDTO.builder()
                .nom("Ballon Elite")
                .description("Nouvelle description")
                .prix(59.90)
                .quantiteStock(15)
                .categorieId(1L)
                .build();

        MaterielDTO result = materielService.update(1L, updatedDTO);

        assertNotNull(result);
        verify(materielRepository, times(1)).save(any());
    }

    @Test
    void updateMateriel_notFound_throwsException() {
        when(materielRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> materielService.update(99L, materielDTO));

        assertEquals("Materiel introuvable", ex.getMessage());
    }

    @Test
    void updateMateriel_categoryNotFound_throwsException() {
        when(materielRepository.findById(1L)).thenReturn(Optional.of(materiel));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> materielService.update(1L, materielDTO));

        assertEquals("Categorie introuvable", ex.getMessage());
    }

    // ── delete ────────────────────────────────────────────

    @Test
    void deleteMaterielTest() {
        when(materielRepository.existsById(1L)).thenReturn(true);

        materielService.delete(1L);

        verify(materielRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMateriel_notFound_throwsException() {
        when(materielRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> materielService.delete(99L));

        assertEquals("Materiel introuvable", ex.getMessage());
        verify(materielRepository, never()).deleteById(any());
    }

    // ── getById ───────────────────────────────────────────

    @Test
    void getMaterielByIdTest() {
        when(materielRepository.findById(1L)).thenReturn(Optional.of(materiel));

        MaterielDTO result = materielService.getById(1L);

        assertNotNull(result);
        assertEquals("Ballon Pro", result.getNom());
        assertEquals(1L, result.getCategorieId());
    }

    @Test
    void getMaterielById_notFound_throwsException() {
        when(materielRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> materielService.getById(99L));

        assertEquals("Materiel introuvable", ex.getMessage());
    }

    // ── getAll ────────────────────────────────────────────

    @Test
    void getAllMaterielsTest() {
        when(materielRepository.findAll()).thenReturn(List.of(materiel));

        List<MaterielDTO> result = materielService.getAll();

        assertEquals(1, result.size());
        assertEquals("Ballon Pro", result.get(0).getNom());
        assertEquals("Football", result.get(0).getCategorieNom());
    }

    @Test
    void getAllMateriels_emptyList() {
        when(materielRepository.findAll()).thenReturn(List.of());

        List<MaterielDTO> result = materielService.getAll();

        assertTrue(result.isEmpty());
    }
}
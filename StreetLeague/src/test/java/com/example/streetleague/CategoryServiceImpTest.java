package com.example.streetleague;

import com.example.streetleague.Repository.CategoryRepository;
import com.example.streetleague.ServiceImp.CategoryServiceImp;
import com.example.streetleague.domain.Category;
import com.example.streetleague.domain.Materiel;
import com.example.streetleague.dto.CategoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImpTest {

    @InjectMocks private CategoryServiceImp categoryService;
    @Mock private CategoryRepository categoryRepository;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setNom("Football");
        category.setDescription("Équipements football");
        category.setMateriels(new ArrayList<>());

        categoryDTO = CategoryDTO.builder()
                .id(1L)
                .nom("Football")
                .description("Équipements football")
                .build();
    }

    // ── create ────────────────────────────────────────────

    @Test
    void createCategoryTest() {
        when(categoryRepository.existsByNom("Football")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(category);

        CategoryDTO result = categoryService.create(categoryDTO);

        assertNotNull(result);
        assertEquals("Football", result.getNom());
        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    void createCategory_duplicateNom_throwsException() {
        when(categoryRepository.existsByNom("Football")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.create(categoryDTO));

        assertEquals("Une catégorie avec ce nom existe déjà", ex.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    // ── update ────────────────────────────────────────────

    @Test
    void updateCategoryTest() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any())).thenReturn(category);

        CategoryDTO updatedDTO = CategoryDTO.builder()
                .nom("Basketball")
                .description("Équipements basketball")
                .build();

        CategoryDTO result = categoryService.update(1L, updatedDTO);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    void updateCategory_notFound_throwsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.update(99L, categoryDTO));

        assertEquals("Catégorie introuvable", ex.getMessage());
    }

    // ── delete ────────────────────────────────────────────

    @Test
    void deleteCategoryTest() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_withMateriels_throwsException() {
        Materiel materiel = new Materiel();
        category.setMateriels(List.of(materiel));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.delete(1L));

        assertTrue(ex.getMessage().contains("Impossible de supprimer"));
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_notFound_throwsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.delete(99L));

        assertEquals("Catégorie introuvable", ex.getMessage());
    }

    // ── getById ───────────────────────────────────────────

    @Test
    void getCategoryByIdTest() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.getById(1L);

        assertNotNull(result);
        assertEquals("Football", result.getNom());
    }

    @Test
    void getCategoryById_notFound_throwsException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.getById(99L));
    }

    // ── getAll ────────────────────────────────────────────

    @Test
    void getAllCategoriesTest() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryDTO> result = categoryService.getAll();

        assertEquals(1, result.size());
        assertEquals("Football", result.get(0).getNom());
    }

    @Test
    void getAllCategories_emptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<CategoryDTO> result = categoryService.getAll();

        assertTrue(result.isEmpty());
    }
}
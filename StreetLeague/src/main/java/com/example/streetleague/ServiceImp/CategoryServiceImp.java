package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.CategoryRepository;
import com.example.streetleague.domain.Category;
import com.example.streetleague.dto.CategoryDTO;
import com.example.streetleague.ServiceInterface.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDTO create(CategoryDTO dto) {

        if (categoryRepository.existsByNom(dto.getNom())) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà");
        }

        Category category = Category.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .build();

        return mapToDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO dto) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        category.setNom(dto.getNom());
        category.setDescription(dto.getDescription());

        return mapToDTO(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        if (category.getMateriels() != null && !category.getMateriels().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : des matériels sont liés à cette catégorie");
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryDTO getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        return mapToDTO(category);
    }

    @Transactional
    @Override
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CategoryDTO mapToDTO(Category category) {

        int nombre = category.getMateriels() == null
                ? 0
                : category.getMateriels().size();

        return CategoryDTO.builder()
                .id(category.getId())
                .nom(category.getNom())
                .description(category.getDescription())
                .nombreMateriels(nombre)
                .build();
    }
}
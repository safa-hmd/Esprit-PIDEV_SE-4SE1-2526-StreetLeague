package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.CategoryRepository;
import com.example.streetleague.Repository.MaterielRepository;
import com.example.streetleague.domain.Category;
import com.example.streetleague.domain.Materiel;
import com.example.streetleague.dto.MaterielDTO;
import com.example.streetleague.ServiceInterface.MaterielService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterielServiceImp implements MaterielService {

    private final MaterielRepository materielRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public MaterielDTO create(MaterielDTO dto) {

        Category category = categoryRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Categorie introuvable"));

        Materiel materiel = Materiel.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .prix(dto.getPrix())
                .quantiteStock(dto.getQuantiteStock())
                .imageUrl(dto.getImageUrl())
                .categorie(category)
                .build();

        return mapToDTO(materielRepository.save(materiel));
    }

    @Override
    public MaterielDTO update(Long id, MaterielDTO dto) {

        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materiel introuvable"));

        Category category = categoryRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Categorie introuvable"));

        materiel.setNom(dto.getNom());
        materiel.setDescription(dto.getDescription());
        materiel.setPrix(dto.getPrix());
        materiel.setQuantiteStock(dto.getQuantiteStock());
        materiel.setImageUrl(dto.getImageUrl());
        materiel.setCategorie(category);

        return mapToDTO(materielRepository.save(materiel));
    }

    @Override
    public void delete(Long id) {
        if (!materielRepository.existsById(id)) {
            throw new RuntimeException("Materiel introuvable");
        }
        materielRepository.deleteById(id);
    }

    @Override
    public MaterielDTO getById(Long id) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materiel introuvable"));
        return mapToDTO(materiel);
    }

    @Override
    public List<MaterielDTO> getAll() {
        return materielRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Mapping centralisé
    private MaterielDTO mapToDTO(Materiel materiel) {
        return MaterielDTO.builder()
                .id(materiel.getId())
                .nom(materiel.getNom())
                .description(materiel.getDescription())
                .prix(materiel.getPrix())
                .quantiteStock(materiel.getQuantiteStock())
                .imageUrl(materiel.getImageUrl())
                .categorieId(materiel.getCategorie().getId())
                .categorieNom(materiel.getCategorie().getNom())
                .build();
    }
}
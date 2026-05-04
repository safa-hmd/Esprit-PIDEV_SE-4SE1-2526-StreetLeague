package com.example.streetleague.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String description;

    private int nombreMateriels; // pour affichage

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String nom, String description, int nombreMateriels) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.nombreMateriels = nombreMateriels;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNombreMateriels() {
        return nombreMateriels;
    }

    public void setNombreMateriels(int nombreMateriels) {
        this.nombreMateriels = nombreMateriels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDTO that = (CategoryDTO) o;
        return nombreMateriels == that.nombreMateriels && Objects.equals(id, that.id) && Objects.equals(nom, that.nom) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, description, nombreMateriels);
    }

    @Override
    public String toString() {
        return "CategoryDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", nombreMateriels=" + nombreMateriels +
                '}';
    }

    public static CategoryDTOBuilder builder() {
        return new CategoryDTOBuilder();
    }

    public static class CategoryDTOBuilder {
        private Long id;
        private String nom;
        private String description;
        private int nombreMateriels;

        public CategoryDTOBuilder id(Long id) { this.id = id; return this; }
        public CategoryDTOBuilder nom(String nom) { this.nom = nom; return this; }
        public CategoryDTOBuilder description(String description) { this.description = description; return this; }
        public CategoryDTOBuilder nombreMateriels(int nombreMateriels) { this.nombreMateriels = nombreMateriels; return this; }

        public CategoryDTO build() {
            return new CategoryDTO(id, nom, description, nombreMateriels);
        }
    }
}
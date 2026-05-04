package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import java.util.Objects;

public class MaterielDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Positive(message = "Le prix doit être positif")
    private double prix;

    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private int quantiteStock;

    private String imageUrl;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;

    private String categorieNom; // pour affichage

    public MaterielDTO() {
    }

    public MaterielDTO(Long id, String nom, String description, double prix, int quantiteStock, String imageUrl, Long categorieId, String categorieNom) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.imageUrl = imageUrl;
        this.categorieId = categorieId;
        this.categorieNom = categorieNom;
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

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Long categorieId) {
        this.categorieId = categorieId;
    }

    public String getCategorieNom() {
        return categorieNom;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterielDTO that = (MaterielDTO) o;
        return Double.compare(that.prix, prix) == 0 && quantiteStock == that.quantiteStock && Objects.equals(id, that.id) && Objects.equals(nom, that.nom) && Objects.equals(description, that.description) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(categorieId, that.categorieId) && Objects.equals(categorieNom, that.categorieNom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, description, prix, quantiteStock, imageUrl, categorieId, categorieNom);
    }

    @Override
    public String toString() {
        return "MaterielDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", quantiteStock=" + quantiteStock +
                '}';
    }

    public static MaterielDTOBuilder builder() {
        return new MaterielDTOBuilder();
    }

    public static class MaterielDTOBuilder {
        private Long id;
        private String nom;
        private String description;
        private double prix;
        private int quantiteStock;
        private String imageUrl;
        private Long categorieId;
        private String categorieNom;

        public MaterielDTOBuilder id(Long id) { this.id = id; return this; }
        public MaterielDTOBuilder nom(String nom) { this.nom = nom; return this; }
        public MaterielDTOBuilder description(String description) { this.description = description; return this; }
        public MaterielDTOBuilder prix(double prix) { this.prix = prix; return this; }
        public MaterielDTOBuilder quantiteStock(int quantiteStock) { this.quantiteStock = quantiteStock; return this; }
        public MaterielDTOBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public MaterielDTOBuilder categorieId(Long categorieId) { this.categorieId = categorieId; return this; }
        public MaterielDTOBuilder categorieNom(String categorieNom) { this.categorieNom = categorieNom; return this; }

        public MaterielDTO build() {
            return new MaterielDTO(id, nom, description, prix, quantiteStock, imageUrl, categorieId, categorieNom);
        }
    }
}
package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "materiels")
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double prix;

    @Column(nullable = false)
    private int quantiteStock;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
            @JsonIgnore
    private Category categorie;

    public Materiel() {
    }

    public Materiel(Long id, String nom, String description, double prix, int quantiteStock, String imageUrl, Category categorie) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.imageUrl = imageUrl;
        this.categorie = categorie;
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

    public Category getCategorie() {
        return categorie;
    }

    public void setCategorie(Category categorie) {
        this.categorie = categorie;
    }

    public static MaterielBuilder builder() {
        return new MaterielBuilder();
    }

    public static class MaterielBuilder {
        private Long id;
        private String nom;
        private String description;
        private double prix;
        private int quantiteStock;
        private String imageUrl;
        private Category categorie;

        public MaterielBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MaterielBuilder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public MaterielBuilder description(String description) {
            this.description = description;
            return this;
        }

        public MaterielBuilder prix(double prix) {
            this.prix = prix;
            return this;
        }

        public MaterielBuilder quantiteStock(int quantiteStock) {
            this.quantiteStock = quantiteStock;
            return this;
        }

        public MaterielBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public MaterielBuilder categorie(Category categorie) {
            this.categorie = categorie;
            return this;
        }

        public Materiel build() {
            return new Materiel(id, nom, description, prix, quantiteStock, imageUrl, categorie);
        }
    }
}
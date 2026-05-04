package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "ligne_panier")
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "panier_id", nullable = false)
            @JsonIgnore
    private Panier panier;


    @ManyToOne
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    @Column(nullable = false)
    private int quantite;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Panier getPanier() {
        return panier;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public LignePanier() {
    }

    public LignePanier(Long id, Panier panier, Materiel materiel, int quantite) {
        this.id = id;
        this.panier = panier;
        this.materiel = materiel;
        this.quantite = quantite;
    }

    public static LignePanierBuilder builder() {
        return new LignePanierBuilder();
    }

    public static class LignePanierBuilder {
        private Long id;
        private Panier panier;
        private Materiel materiel;
        private int quantite;

        public LignePanierBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LignePanierBuilder panier(Panier panier) {
            this.panier = panier;
            return this;
        }

        public LignePanierBuilder materiel(Materiel materiel) {
            this.materiel = materiel;
            return this;
        }

        public LignePanierBuilder quantite(int quantite) {
            this.quantite = quantite;
            return this;
        }

        public LignePanier build() {
            return new LignePanier(id, panier, materiel, quantite);
        }
    }
}
package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "ligne_commande")
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
            @JsonIgnore
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "materiel_id", nullable = false)
    private Materiel materiel;

    private int quantite;

    private double prixUnitaire;

    public LigneCommande() {
    }

    public LigneCommande(Long id, Commande commande, Materiel materiel, int quantite, double prixUnitaire) {
        this.id = id;
        this.commande = commande;
        this.materiel = materiel;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
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

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public static LigneCommandeBuilder builder() {
        return new LigneCommandeBuilder();
    }

    public static class LigneCommandeBuilder {
        private Long id;
        private Commande commande;
        private Materiel materiel;
        private int quantite;
        private double prixUnitaire;

        public LigneCommandeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LigneCommandeBuilder commande(Commande commande) {
            this.commande = commande;
            return this;
        }

        public LigneCommandeBuilder materiel(Materiel materiel) {
            this.materiel = materiel;
            return this;
        }

        public LigneCommandeBuilder quantite(int quantite) {
            this.quantite = quantite;
            return this;
        }

        public LigneCommandeBuilder prixUnitaire(double prixUnitaire) {
            this.prixUnitaire = prixUnitaire;
            return this;
        }

        public LigneCommande build() {
            return new LigneCommande(id, commande, materiel, quantite, prixUnitaire);
        }
    }
}
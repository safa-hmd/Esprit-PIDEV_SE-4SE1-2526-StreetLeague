package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "livraisons")
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "transporteur_id", nullable = false)
    private Transporteur transporteur;

    @ManyToOne
    @JoinColumn(name = "livreur_id")
    private User livreur;

    private String adresse;

    private double fraisLivraison;

    @Enumerated(EnumType.STRING)
    private LivraisonStatus statut;

    public Livraison() {
    }

    public Livraison(Long id, Commande commande, Transporteur transporteur, User livreur, String adresse, double fraisLivraison, LivraisonStatus statut) {
        this.id = id;
        this.commande = commande;
        this.transporteur = transporteur;
        this.livreur = livreur;
        this.adresse = adresse;
        this.fraisLivraison = fraisLivraison;
        this.statut = statut;
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

    public Transporteur getTransporteur() {
        return transporteur;
    }

    public void setTransporteur(Transporteur transporteur) {
        this.transporteur = transporteur;
    }

    public User getLivreur() {
        return livreur;
    }

    public void setLivreur(User livreur) {
        this.livreur = livreur;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public double getFraisLivraison() {
        return fraisLivraison;
    }

    public void setFraisLivraison(double fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public LivraisonStatus getStatut() {
        return statut;
    }

    public void setStatut(LivraisonStatus statut) {
        this.statut = statut;
    }

    @JsonProperty("commandeId")
    public Long getCommandeId() {
        return commande != null ? commande.getId() : null;
    }

    @JsonProperty("transporteurId")
    public Long getTransporteurId() {
        return transporteur != null ? transporteur.getId() : null;
    }

    public static LivraisonBuilder builder() {
        return new LivraisonBuilder();
    }

    public static class LivraisonBuilder {
        private Long id;
        private Commande commande;
        private Transporteur transporteur;
        private User livreur;
        private String adresse;
        private double fraisLivraison;
        private LivraisonStatus statut;

        public LivraisonBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LivraisonBuilder commande(Commande commande) {
            this.commande = commande;
            return this;
        }

        public LivraisonBuilder transporteur(Transporteur transporteur) {
            this.transporteur = transporteur;
            return this;
        }

        public LivraisonBuilder livreur(User livreur) {
            this.livreur = livreur;
            return this;
        }

        public LivraisonBuilder adresse(String adresse) {
            this.adresse = adresse;
            return this;
        }

        public LivraisonBuilder fraisLivraison(double fraisLivraison) {
            this.fraisLivraison = fraisLivraison;
            return this;
        }

        public LivraisonBuilder statut(LivraisonStatus statut) {
            this.statut = statut;
            return this;
        }

        public Livraison build() {
            return new Livraison(id, commande, transporteur, livreur, adresse, fraisLivraison, statut);
        }
    }
}
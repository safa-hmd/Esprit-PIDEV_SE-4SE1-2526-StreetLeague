package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "paniers")
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
            @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL)
            @JsonIgnore
    private List<LignePanier> lignes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<LignePanier> getLignes() {
        return lignes;
    }

    public void setLignes(List<LignePanier> lignes) {
        this.lignes = lignes;
    }

    public Panier(Long id, User user, List<LignePanier> lignes) {
        this.id = id;
        this.user = user;
        this.lignes = lignes;
    }

    public Panier() {
    }

    public static PanierBuilder builder() {
        return new PanierBuilder();
    }

    public static class PanierBuilder {
        private Long id;
        private User user;
        private List<LignePanier> lignes;

        public PanierBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PanierBuilder user(User user) {
            this.user = user;
            return this;
        }

        public PanierBuilder lignes(List<LignePanier> lignes) {
            this.lignes = lignes;
            return this;
        }

        public Panier build() {
            return new Panier(id, user, lignes);
        }
    }
}
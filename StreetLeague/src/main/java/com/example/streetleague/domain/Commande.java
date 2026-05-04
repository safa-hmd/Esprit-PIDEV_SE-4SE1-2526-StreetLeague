package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
            @JsonIgnore
    private User user;

    private double montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommandeStatus statut;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
            @JsonIgnore
    private List<LigneCommande> lignes;

    public Commande() {
    }

    public Commande(Long id, User user, double montantTotal, CommandeStatus statut, List<LigneCommande> lignes) {
        this.id = id;
        this.user = user;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.lignes = lignes;
    }

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

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public CommandeStatus getStatut() {
        return statut;
    }

    public void setStatut(CommandeStatus statut) {
        this.statut = statut;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    public static CommandeBuilder builder() {
        return new CommandeBuilder();
    }

    public static class CommandeBuilder {
        private Long id;
        private User user;
        private double montantTotal;
        private CommandeStatus statut;
        private List<LigneCommande> lignes;

        public CommandeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CommandeBuilder user(User user) {
            this.user = user;
            return this;
        }

        public CommandeBuilder montantTotal(double montantTotal) {
            this.montantTotal = montantTotal;
            return this;
        }

        public CommandeBuilder statut(CommandeStatus statut) {
            this.statut = statut;
            return this;
        }

        public CommandeBuilder lignes(List<LigneCommande> lignes) {
            this.lignes = lignes;
            return this;
        }

        public Commande build() {
            return new Commande(id, user, montantTotal, statut, lignes);
        }
    }
}
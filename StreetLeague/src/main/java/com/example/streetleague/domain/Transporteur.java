package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "transporteurs")
public class Transporteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomSociete;

    private String telephone;
    private String email;

    public Transporteur() {
    }

    public Transporteur(Long id, String nomSociete, String telephone, String email) {
        this.id = id;
        this.nomSociete = nomSociete;
        this.telephone = telephone;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomSociete() {
        return nomSociete;
    }

    public void setNomSociete(String nomSociete) {
        this.nomSociete = nomSociete;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static TransporteurBuilder builder() {
        return new TransporteurBuilder();
    }

    public static class TransporteurBuilder {
        private Long id;
        private String nomSociete;
        private String telephone;
        private String email;

        public TransporteurBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TransporteurBuilder nomSociete(String nomSociete) {
            this.nomSociete = nomSociete;
            return this;
        }

        public TransporteurBuilder telephone(String telephone) {
            this.telephone = telephone;
            return this;
        }

        public TransporteurBuilder email(String email) {
            this.email = email;
            return this;
        }

        public Transporteur build() {
            return new Transporteur(id, nomSociete, telephone, email);
        }
    }
}
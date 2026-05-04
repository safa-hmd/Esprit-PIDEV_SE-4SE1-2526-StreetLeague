package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class SponsorDTO {
    private Long id;
    private String nom;
    private String type;
    @JsonProperty("contactEmail")
    private String contactEmail;
    private String telephone;
    private String adresse;

    public SponsorDTO() {
    }

    public SponsorDTO(Long id, String nom, String type, String contactEmail, String telephone, String adresse) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.contactEmail = contactEmail;
        this.telephone = telephone;
        this.adresse = adresse;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SponsorDTO that = (SponsorDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(nom, that.nom) && Objects.equals(type, that.type) && Objects.equals(contactEmail, that.contactEmail) && Objects.equals(telephone, that.telephone) && Objects.equals(adresse, that.adresse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, type, contactEmail, telephone, adresse);
    }

    @Override
    public String toString() {
        return "SponsorDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                '}';
    }

    public static SponsorDTOBuilder builder() {
        return new SponsorDTOBuilder();
    }

    public static class SponsorDTOBuilder {
        private Long id;
        private String nom;
        private String type;
        private String contactEmail;
        private String telephone;
        private String adresse;

        public SponsorDTOBuilder id(Long id) { this.id = id; return this; }
        public SponsorDTOBuilder nom(String nom) { this.nom = nom; return this; }
        public SponsorDTOBuilder type(String type) { this.type = type; return this; }
        public SponsorDTOBuilder contactEmail(String contactEmail) { this.contactEmail = contactEmail; return this; }
        public SponsorDTOBuilder telephone(String telephone) { this.telephone = telephone; return this; }
        public SponsorDTOBuilder adresse(String adresse) { this.adresse = adresse; return this; }

        public SponsorDTO build() {
            return new SponsorDTO(id, nom, type, contactEmail, telephone, adresse);
        }
    }
}
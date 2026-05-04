package com.example.streetleague.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

public class TransporteurDTO {

    @NotBlank(message = "Le nom de la société est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit faire entre 2 et 100 caractères")
    private String nomSociete;

    @Size(max = 20, message = "Le téléphone doit contenir au maximum 20 caractères")
    private String telephone;

    @Email(message = "Email invalide")
    private String email;

    public TransporteurDTO() {
    }

    public TransporteurDTO(String nomSociete, String telephone, String email) {
        this.nomSociete = nomSociete;
        this.telephone = telephone;
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransporteurDTO that = (TransporteurDTO) o;
        return Objects.equals(nomSociete, that.nomSociete) && Objects.equals(telephone, that.telephone) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomSociete, telephone, email);
    }

    @Override
    public String toString() {
        return "TransporteurDTO{" +
                "nomSociete='" + nomSociete + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public static TransporteurDTOBuilder builder() {
        return new TransporteurDTOBuilder();
    }

    public static class TransporteurDTOBuilder {
        private String nomSociete;
        private String telephone;
        private String email;

        public TransporteurDTOBuilder nomSociete(String nomSociete) { this.nomSociete = nomSociete; return this; }
        public TransporteurDTOBuilder telephone(String telephone) { this.telephone = telephone; return this; }
        public TransporteurDTOBuilder email(String email) { this.email = email; return this; }

        public TransporteurDTO build() {
            return new TransporteurDTO(nomSociete, telephone, email);
        }
    }
}
package com.example.streetleague.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "evenement_communaute")
public class EvenementCommunaute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String description;

    @FutureOrPresent(message = "La date doit être future ou présente")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "communaute_id", nullable = false)
    private Communaute communaute;

    @NotNull(message = "L'organisateur est obligatoire")
    private Long organisateurId;

    public EvenementCommunaute() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Communaute getCommunaute() {
        return communaute;
    }

    public void setCommunaute(Communaute communaute) {
        this.communaute = communaute;
    }

    public Long getOrganisateurId() {
        return organisateurId;
    }

    public void setOrganisateurId(Long organisateurId) {
        this.organisateurId = organisateurId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvenementCommunaute that = (EvenementCommunaute) o;
        return Objects.equals(id, that.id) && Objects.equals(titre, that.titre) && Objects.equals(description, that.description) && Objects.equals(date, that.date) && Objects.equals(communaute, that.communaute) && Objects.equals(organisateurId, that.organisateurId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titre, description, date, communaute, organisateurId);
    }

    @Override
    public String toString() {
        return "EvenementCommunaute{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", communaute=" + communaute +
                ", organisateurId=" + organisateurId +
                '}';
    }
}


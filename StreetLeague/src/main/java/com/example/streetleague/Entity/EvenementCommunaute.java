package com.example.streetleague.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Communaute getCommunaute() { return communaute; }
    public void setCommunaute(Communaute communaute) { this.communaute = communaute; }
    public Long getOrganisateurId() { return organisateurId; }
    public void setOrganisateurId(Long organisateurId) { this.organisateurId = organisateurId; }
}

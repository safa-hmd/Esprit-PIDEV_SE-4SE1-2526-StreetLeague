package com.example.streetleague.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "communaute")
public class Communaute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 50, message = "Le nom doit contenir entre 3 et 50 caractères")
    private String nom;

    private String description;
    private String type;

    @PastOrPresent(message = "La date de création doit être passée ou présente")
    private Date dateCreation;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createurId;

    @OneToMany(mappedBy = "communaute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvenementCommunaute> evenements;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public Long getCreateurId() { return createurId; }
    public void setCreateurId(Long createurId) { this.createurId = createurId; }
    public List<EvenementCommunaute> getEvenements() { return evenements; }
    public void setEvenements(List<EvenementCommunaute> evenements) { this.evenements = evenements; }
}

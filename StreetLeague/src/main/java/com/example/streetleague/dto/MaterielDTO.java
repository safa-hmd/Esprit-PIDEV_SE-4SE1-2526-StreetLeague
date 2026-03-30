package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterielDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Positive(message = "Le prix doit être positif")
    private double prix;

    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private int quantiteStock;

    private String imageUrl;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;

    private String categorieNom; // pour affichage
}
package com.example.streetleague.dto;

import com.example.streetleague.domain.LivraisonStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivraisonDTO {

    @NotNull(message = "Commande est obligatoire")
    private Long commandeId;

    @NotNull(message = "Transporteur est obligatoire")
    private Long transporteurId;

    private Long livreurId; // facultatif

    @NotBlank(message = "Adresse est obligatoire")
    private String adresse;

    @Min(value = 0, message = "Frais de livraison doit être >= 0")
    private double fraisLivraison;

    @NotNull(message = "Statut est obligatoire")
    private LivraisonStatus statut;
}
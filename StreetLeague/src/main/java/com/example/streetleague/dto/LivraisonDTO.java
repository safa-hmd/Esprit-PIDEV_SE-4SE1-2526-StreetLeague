package com.example.streetleague.dto;

import com.example.streetleague.domain.LivraisonStatus;
import com.example.streetleague.domain.Priorite;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivraisonDTO {

    // ✅ Pas de @NotNull — le cart peut envoyer null si checkout n'a pas encore renvoyé l'ID
    private Long commandeId;

    private Long livreurId;       // facultatif — dispatcher assignera

    private String adresse;

    private Double latitudeClient;
    private Double longitudeClient;

    @Min(value = 0, message = "Frais de livraison doit être >= 0")
    private double fraisLivraison;

    private LivraisonStatus statut;

    private Priorite priorite;

    private String motifEchec;
}
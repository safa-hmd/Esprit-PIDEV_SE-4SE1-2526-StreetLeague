package com.example.streetleague.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeDTO {

    @NotNull(message = "User est obligatoire")
    private Long userId;

    @NotNull(message = "Statut est obligatoire")
    private String statut;

    @NotNull(message = "Lignes de commande sont obligatoires")
    private List<LigneCommandeDTO> lignes;
}
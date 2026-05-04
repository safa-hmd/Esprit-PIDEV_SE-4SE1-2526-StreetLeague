package com.example.streetleague.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeDTO {

    @NotNull
    private Long userId;

    private String statut;

    private List<LigneCommandeDTO> lignes;

    // ── PARTIE 9 : GPS client depuis navigateur ──────────
    private Double latitudeClient;
    private Double longitudeClient;
    private String adresseLivraison;
}
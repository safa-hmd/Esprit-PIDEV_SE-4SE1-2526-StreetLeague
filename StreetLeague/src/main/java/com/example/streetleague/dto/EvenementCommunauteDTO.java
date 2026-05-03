package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementCommunauteDTO {
    private Long id;
    private String titre;
    private String description;
    private String lieu;
    @JsonProperty("dateEvenement")
    private LocalDateTime dateEvenement;
    private String statut;
    @JsonProperty("communauteId")
    private Long communauteId;
    @JsonProperty("communauteNom")
    private String communauteNom;
    @JsonProperty("organisateurId")
    private Long organisateurId;
    @JsonProperty("organisateurNom")
    private String organisateurNom;
    @JsonProperty("dateCreation")
    private LocalDateTime dateCreation;
    private Integer capaciteMax;
    private Integer participantsCount;
    private String typeEvenement;
}

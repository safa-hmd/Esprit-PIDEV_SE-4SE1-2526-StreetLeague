package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour le classement des communautés les plus sponsorisées
 * Requête avancée #2 : JOIN 3 tables + HAVING (Communaute → EvenementCommunaute → SponsoringEvenement)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCommunauteDTO {
    private String communauteNom;
    private String communauteType;
    private Long nombreEvenements;
    private Long nombreSponsorings;
    private Double moyenneContribution;
    private BigDecimal maxContribution;
}

package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour les statistiques de contribution par communauté
 * Requête avancée #1 : JOIN 3 tables (SponsoringEvenement → EvenementCommunaute → Communaute)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunauteStatsDTO {
    private String communauteNom;
    private String communauteType;
    private Long nombreEvenements;
    private Long nombreSponsorings;
    private BigDecimal totalContribution;
}

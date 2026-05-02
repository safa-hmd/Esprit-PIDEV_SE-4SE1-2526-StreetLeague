package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour le dashboard complet sponsor-communauté
 * Requête avancée #6 : JOIN 4 tables (Sponsor → SponsoringEvenement → EvenementCommunaute → Communaute)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSponsorCommunauteDTO {
    private String sponsorNom;
    private String sponsorType;
    private String communauteNom;
    private String communauteType;
    private Long nombreEvenements;
    private BigDecimal totalContribution;
    private Double moyenneContribution;
}

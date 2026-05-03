package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour la comparaison contrats vs sponsorings par sponsor
 * Requête avancée #5 : 3 tables avec sous-requêtes corrélées (Sponsor → ContratSponsor + SponsoringEvenement)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparaisonSponsorDTO {
    private String sponsorNom;
    private String sponsorType;
    private BigDecimal totalContrats;
    private BigDecimal totalSponsorings;
    private Long nombreContrats;
    private Long nombreSponsorings;
}

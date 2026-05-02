package com.example.streetleague.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO pour les événements qui n'ont reçu aucun sponsoring
 * Requête avancée #4 : LEFT JOIN 3 tables (Communaute → EvenementCommunaute ←LEFT→ SponsoringEvenement)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementSansSponsoringDTO {
    private String communauteNom;
    private String evenementTitre;
    private Date evenementDate;
    private String evenementDescription;
}

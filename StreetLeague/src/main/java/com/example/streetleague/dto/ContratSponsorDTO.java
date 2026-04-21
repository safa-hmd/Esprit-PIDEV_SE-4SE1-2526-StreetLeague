package com.example.streetleague.dto;

import java.math.BigDecimal;
import java.util.Date;

public record ContratSponsorDTO(
        Long id,
        Long sponsorId,
        Long equipeId,
        BigDecimal montant,
        Date dateDebut,
        Date dateFin,
        String statut,
        String conditions
) {}

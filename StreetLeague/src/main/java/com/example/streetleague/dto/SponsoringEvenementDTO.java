package com.example.streetleague.dto;

import java.math.BigDecimal;

public record SponsoringEvenementDTO(
        Long id,
        Long sponsorId,
        Long evenementId,
        BigDecimal contribution,
        String typeContribution
) {}

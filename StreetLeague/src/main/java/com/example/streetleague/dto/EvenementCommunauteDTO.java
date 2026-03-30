package com.example.streetleague.dto;

import java.util.Date;

public record EvenementCommunauteDTO(
        Long id,
        String titre,
        String description,
        Date date,
        Long communauteId,
        Long organisateurId
) {}

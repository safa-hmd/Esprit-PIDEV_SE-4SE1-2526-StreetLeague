package com.example.streetleague.dto;

import java.util.Date;

public record CommunauteDTO(
        Long id,
        String nom,
        String description,
        String type,
        Date dateCreation,
        Long createurId
) {}

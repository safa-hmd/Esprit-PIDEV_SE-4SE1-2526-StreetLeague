package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartDTO {

    @NotNull(message = "User obligatoire")
    private Long userId;

    @NotNull(message = "Materiel obligatoire")
    private Long materielId;

    @Min(value = 1, message = "Quantité doit être >= 1")
    private int quantite;
}
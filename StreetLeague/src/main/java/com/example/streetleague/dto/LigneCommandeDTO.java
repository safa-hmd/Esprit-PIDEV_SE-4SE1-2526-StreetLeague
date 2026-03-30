package com.example.streetleague.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommandeDTO {

    @NotNull(message = "Materiel est obligatoire")
    private Long materielId;

    @Min(value = 1, message = "Quantité doit être >= 1")
    private int quantite;
}
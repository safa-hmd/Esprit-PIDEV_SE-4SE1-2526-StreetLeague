package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartDTO {

    @NotNull
    private Long lignePanierId;

    @Min(value = 1, message = "Quantité >= 1")
    private int quantite;
}
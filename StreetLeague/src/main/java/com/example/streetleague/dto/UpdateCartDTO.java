package com.example.streetleague.dto;

import jakarta.validation.constraints.*;
import lombok.*;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
public class UpdateCartDTO {

    @NotNull
    private Long lignePanierId;

    @Min(value = 1, message = "Quantité >= 1")
    private int quantite;

    public Long getLignePanierId() {
        return lignePanierId;
    }

    public void setLignePanierId(Long lignePanierId) {
        this.lignePanierId = lignePanierId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public UpdateCartDTO(Long lignePanierId, int quantite) {
        this.lignePanierId = lignePanierId;
        this.quantite = quantite;
    }

    public UpdateCartDTO() {
    }
}
package com.example.streetleague.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanierResponseDTO {

    private Long panierId;
    private Long userId;
    private List<LignePanierResponseDTO> lignes;
    private double total;
}
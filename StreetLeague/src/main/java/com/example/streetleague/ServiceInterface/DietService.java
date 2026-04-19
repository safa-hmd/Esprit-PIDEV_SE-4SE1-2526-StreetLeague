package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.DietRequestDTO;
import com.example.streetleague.dto.DietResponseDTO;

public interface DietService {
    DietResponseDTO getDietRecommendation(DietRequestDTO request, Long userId);
    DietResponseDTO getLastDiet(Long userId);
}
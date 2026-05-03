package com.example.streetleague.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Long idUser;
    private String fullName;
    private String email;
    private String role;
    // Statistiques
    private int teamCount;
    private int matchCount;
    private int trainingCount;
}
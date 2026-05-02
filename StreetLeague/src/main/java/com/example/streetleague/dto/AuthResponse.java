/*package com.example.streetleague.dto;


public record AuthResponse(
        String token,
        String email,
        String role
) {}*/

package com.example.streetleague.dto;

public record AuthResponse(
        String token,  // ← 1er
        String email,  // ← 2ème
        String role,   // ← 3ème
        Long idUser    // ← 4ème
) {}

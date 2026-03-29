/*package com.example.streetleague.dto;


public record AuthResponse(
        String token,
        String email,
        String role
) {}*/

package com.example.streetleague.dto;

public record AuthResponse(
        Long id,
        String token,
        String email,
        String role
) {}

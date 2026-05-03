package com.example.streetleague.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransporteurDTO {

    @NotBlank(message = "Le nom de la société est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit faire entre 2 et 100 caractères")
    private String nomSociete;

    @Size(max = 20, message = "Le téléphone doit contenir au maximum 20 caractères")
    private String telephone;

    @Email(message = "Email invalide")
    private String email;
}
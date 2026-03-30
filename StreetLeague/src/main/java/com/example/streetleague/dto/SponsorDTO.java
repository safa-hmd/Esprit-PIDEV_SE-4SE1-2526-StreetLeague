package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SponsorDTO {
    private Long id;
    private String nom;
    private String type;
    @JsonProperty("contactEmail")
    private String contactEmail;
    private String telephone;
    private String adresse;
}
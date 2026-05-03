package com.example.streetleague.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunauteDTO {
    private Long id;
    private String nom;
    private String description;
    private String type;
    private String statut;
    @JsonProperty("createurId")
    private Long createurId;
    @JsonProperty("createurNom")
    private String createurNom;
    @JsonProperty("dateCreation")
    private LocalDateTime dateCreation;
    private Integer membresCount;
    private String image;
    private String rules;
    private Boolean isActive;
    @JsonProperty("derniereActivite")
    private LocalDateTime derniereActivite;
}

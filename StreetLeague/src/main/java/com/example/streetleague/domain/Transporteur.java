package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transporteurs")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transporteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String nomSociete;

    String telephone;
    String email;
}
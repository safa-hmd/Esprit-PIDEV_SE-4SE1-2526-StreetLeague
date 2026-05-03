package com.example.streetleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "ligne_panier")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "panier_id", nullable = false)
            @JsonIgnore
    Panier panier;


    @ManyToOne
    @JoinColumn(name = "materiel_id", nullable = false)
    Materiel materiel;

    @Column(nullable = false)
    int quantite;
}
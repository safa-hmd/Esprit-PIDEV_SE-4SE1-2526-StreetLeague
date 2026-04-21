package com.example.streetleague.Entity;

import com.example.streetleague.Entity.FieldReservation;
import com.example.streetleague.Entity.SportType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Column(nullable = false)
    private String location;

    private String imageUrl;

    private Double pricePerHour;

    private int capacity; // nombre de joueurs max

    @Column(nullable = false)
    private boolean available;

    // ---- Relations ----

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FieldReservation> reservations = new ArrayList<>();
}

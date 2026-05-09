package com.example.streetleague.domain;

import jakarta.persistence.*;
import lombok.Data;
//import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class PromoUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PromoCode promoCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Commande commande;

    private double appliedAmount;
    private LocalDateTime usedAt;
}
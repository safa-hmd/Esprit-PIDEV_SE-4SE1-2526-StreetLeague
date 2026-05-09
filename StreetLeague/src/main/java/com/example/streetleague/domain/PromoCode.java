package com.example.streetleague.domain;

import jakarta.persistence.*;
import lombok.Data;

//import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // ex: WELCOME10_U123

    @Enumerated(EnumType.STRING)
    private DiscountType type; // PERCENTAGE ou FIXED

    private double value; // 10.0 (pour 10% ou 10 TND)

    private int maxGlobalUsage = 1;
    private int currentGlobalUsage = 0;
    private int maxUsagePerUser = 1;

    private LocalDateTime expirationDate;
    private boolean active = true;

    private double minCartAmount = 0.0; // Pour BIGCART20
    private String source; // "AUTO_WELCOME", "AUTO_CART_ABANDON", "AUTO_FIDELITY", "MANUAL"

    @ElementCollection
    private List<Long> allowedCategoryIds; // Vide = tout le panier
}


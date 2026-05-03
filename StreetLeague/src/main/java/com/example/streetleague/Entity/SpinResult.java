package com.example.streetleague.Entity;

import com.example.streetleague.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class SpinResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String result;      // "BADGE", "FREE_DELIVERY", "COUPON_5", "COUPON_10", "TRY_AGAIN"
    private boolean canRetry;   // true si résultat = TRY_AGAIN
    private boolean hasSpun;    // a-t-il déjà tourné ?
    private LocalDate spinDate;

    @OneToOne
    private User user;
}

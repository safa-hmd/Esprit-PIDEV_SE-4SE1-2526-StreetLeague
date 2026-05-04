package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.CommandeRepository;
import com.example.streetleague.Repository.PromoCodeRepository;
import com.example.streetleague.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromoEngineService {

    private final PromoCodeRepository promoRepo;
    private final CommandeRepository commandeRepo;

    // ─────────────────────────────
    // WELCOME10 (24h après inscription)
    // ─────────────────────────────
    public void generateWelcomePromo(User user) {

        boolean exists = promoRepo.existsByCodeContainingAndSource(
                user.getId().toString(),
                "AUTO_WELCOME"
        );

        if (exists) return;

        PromoCode promo = new PromoCode();
        promo.setCode("WELCOME10_" + user.getId());
        promo.setType(DiscountType.PERCENTAGE);
        promo.setValue(10);
        promo.setMaxUsagePerUser(1);
        promo.setMaxGlobalUsage(1);
        promo.setSource("AUTO_WELCOME");
        promo.setActive(true);
        promo.setMinCartAmount(0);
        promo.setExpirationDate(LocalDateTime.now().plusDays(7));

        promoRepo.save(promo);
    }

    // ─────────────────────────────
    // PANIER ABANDONNÉ (48h)
    // ─────────────────────────────
    public void generateAbandonedCartPromo(User user) {

        boolean exists = promoRepo.existsByCodeContainingAndSource(
                user.getId().toString(),
                "AUTO_CART_ABANDON"
        );

        if (exists) return;

        PromoCode promo = new PromoCode();
        promo.setCode("PANIER10_" + user.getId());
        promo.setType(DiscountType.PERCENTAGE);
        promo.setValue(10);
        promo.setSource("AUTO_CART_ABANDON");
        promo.setActive(true);
        promo.setMinCartAmount(50);
        promo.setExpirationDate(LocalDateTime.now().plusDays(3));

        promoRepo.save(promo);
    }

    // ─────────────────────────────
    // FIDELITE15 (3 commandes livrées)
    // ─────────────────────────────
    public void generateFidelityPromo(User user) {

        long delivered = commandeRepo.countByUserIdAndStatut(
                user.getId(),
                CommandeStatus.LIVREE
        );

        boolean exists = promoRepo.existsByCodeContainingAndSource(
                user.getId().toString(),
                "AUTO_FIDELITY"
        );

        if (delivered >= 3 && !exists) {

            PromoCode promo = new PromoCode();
            promo.setCode("FIDELITE15_" + user.getId());
            promo.setType(DiscountType.PERCENTAGE);
            promo.setValue(15);
            promo.setSource("AUTO_FIDELITY");
            promo.setActive(true);
            promo.setExpirationDate(LocalDateTime.now().plusDays(10));

            promoRepo.save(promo);
        }
    }

    // ─────────────────────────────
    // BIG CART (≥200 TND)
    // ─────────────────────────────
    public void generateBigCartPromo(User user, double total) {

        boolean exists = promoRepo.existsByCodeContainingAndSource(
                user.getId().toString(),
                "AUTO_BIG_CART"
        );

        if (exists) return;

        PromoCode promo = new PromoCode();
        promo.setCode("BIGCART20_" + UUID.randomUUID().toString().substring(0, 6));
        promo.setType(DiscountType.PERCENTAGE);
        promo.setValue(20);
        promo.setSource("AUTO_BIG_CART");
        promo.setActive(true);
        promo.setMinCartAmount(200);
        promo.setExpirationDate(LocalDateTime.now().plusDays(5));

        promoRepo.save(promo);
    }
}
package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.PromoCodeRepository;
import com.example.streetleague.Repository.PromoUsageRepository;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.PromoOfferDTO;
import com.example.streetleague.dto.PromoValidationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromoService {

    private final PromoCodeRepository promoRepo;
    private final PromoUsageRepository usageRepo;

    // =========================================================
    // VALIDATION PROMO (inchangé mais propre)
    // =========================================================
    public PromoValidationDTO validate(
            String codeInput,
            Long userId,
            double cartTotal,
            List<Long> cartCategoryIds
    ) {

        PromoCode promo = promoRepo.findByCodeIgnoreCase(codeInput)
                .orElseThrow(() -> new RuntimeException("Code promo invalide"));

        if (!promo.isActive())
            throw new RuntimeException("Code désactivé");

        if (promo.getExpirationDate() != null &&
                promo.getExpirationDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Code expiré");

        if (promo.getMaxGlobalUsage() > 0 &&
                promo.getCurrentGlobalUsage() >= promo.getMaxGlobalUsage())
            throw new RuntimeException("Usage maximum atteint");

        long userUsage = usageRepo.countByPromoCodeIdAndUserId(promo.getId(), userId);

        if (userUsage >= promo.getMaxUsagePerUser())
            throw new RuntimeException("Déjà utilisé par cet utilisateur");

        if (promo.getMinCartAmount() > 0 && cartTotal < promo.getMinCartAmount())
            throw new RuntimeException("Panier minimum non atteint");

        if (promo.getAllowedCategoryIds() != null && !promo.getAllowedCategoryIds().isEmpty()) {
            boolean allowed = cartCategoryIds.stream()
                    .anyMatch(promo.getAllowedCategoryIds()::contains);

            if (!allowed)
                throw new RuntimeException("Produit non éligible");
        }

        double discount = promo.getType() == DiscountType.PERCENTAGE
                ? cartTotal * (promo.getValue() / 100.0)
                : promo.getValue();

        discount = Math.min(discount, cartTotal);

        return PromoValidationDTO.builder()
                .valid(true)
                .code(promo.getCode())
                .discountAmount(round(discount))
                .newTotal(round(cartTotal - discount))
                .build();
    }

    // =========================================================
    // 🔥 PROMOS CLIENT (CORRIGÉE - VERSION QUI MARCHE)
    // =========================================================
    public List<PromoOfferDTO> getActivePersonalOffers(Long userId) {

        List<PromoCode> promos = promoRepo.findAll();

        return promos.stream()
                .filter(p -> p.isActive())
                .filter(p -> p.getExpirationDate() == null ||
                        p.getExpirationDate().isAfter(LocalDateTime.now()))
                .filter(p -> p.getCode().contains(String.valueOf(userId))
                        || p.getSource().startsWith("AUTO"))
                .map(p -> {

                    return PromoOfferDTO.builder()
                            .code(p.getCode())
                            .type(p.getType().name())
                            .value(p.getValue())
                            .minCartAmount(p.getMinCartAmount())
                            .expirationDate(p.getExpirationDate())
                            .used(false)
                            .expired(false)
                            .source(p.getSource())
                            .build();
                })
                .toList();
    }

    // =========================================================
    // LOGIQUE DE FILTRE (IMPORTANT)
    // =========================================================
    private boolean isValidPromo(PromoCode p, Long userId) {

        if (p == null) return false;

        // ✔ actif uniquement
        if (!p.isActive()) return false;

        // ✔ pas expiré
        if (p.getExpirationDate() != null &&
                p.getExpirationDate().isBefore(LocalDateTime.now()))
            return false;

        // ✔ logique simple : promo personnelle ou auto
        boolean isPersonal = p.getCode() != null &&
                p.getCode().contains(String.valueOf(userId));

        boolean isAuto = p.getSource() != null &&
                p.getSource().startsWith("AUTO_");

        return isPersonal || isAuto;
    }

    // =========================================================
    // UTIL
    // =========================================================
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


}
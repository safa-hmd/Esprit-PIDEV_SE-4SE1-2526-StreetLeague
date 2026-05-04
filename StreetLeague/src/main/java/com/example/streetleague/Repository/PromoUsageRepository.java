package com.example.streetleague.Repository;

import com.example.streetleague.domain.PromoUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoUsageRepository extends JpaRepository<PromoUsage, Long> {
    long countByPromoCodeIdAndUserId(Long promoCodeId, Long userId);

    // Vérifier si un user a déjà utilisé un code d'une source auto
    boolean existsByUserIdAndPromoCodeSource(Long userId, String source);
}
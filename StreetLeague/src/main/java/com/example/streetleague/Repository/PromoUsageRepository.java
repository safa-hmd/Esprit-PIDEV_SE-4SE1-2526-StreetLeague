package com.example.streetleague.Repository;

import com.example.streetleague.domain.PromoUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoUsageRepository extends JpaRepository<PromoUsage, Long> {
    long countByPromoCodeIdAndUserIdUser(Long promoCodeId, Long userId);

    // Vérifier si un user a déjà utilisé un code d'une source auto
    boolean existsByUserIdUserAndPromoCodeSource(Long userId, String source);
}
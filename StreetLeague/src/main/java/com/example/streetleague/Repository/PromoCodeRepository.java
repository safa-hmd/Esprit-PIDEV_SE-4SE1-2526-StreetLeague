package com.example.streetleague.Repository;

import com.example.streetleague.domain.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCodeIgnoreCase(String code);

    // Vérifier existence par code exact
    boolean existsByCode(String code);

    // Vérifier si un user a déjà un code d'une source donnée
    boolean existsByCodeContainingAndSource(String codePart, String source);
}

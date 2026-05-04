package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.*;
import com.example.streetleague.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromoAutoGeneratorService {

    private final PromoCodeRepository promoRepo;
    private final PromoUsageRepository usageRepo;
    private final UserRepository userRepo;
    private final PanierRepository panierRepo;
    private final CommandeRepository commandeRepo;

    // ========================================================================
    // CAS 1 : WELCOME10 - 24h post-inscription
    // ========================================================================
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void generateWelcomeCodes() {
        LocalDateTime start = LocalDateTime.now().minusHours(25);
        LocalDateTime end = LocalDateTime.now().minusHours(23);

        List<User> eligible = userRepo.findAll()
                .stream()
                .filter(u -> u.getRole() != null)  // ← Filtre les rôles null
                .filter(u -> u.getCreatedAt() != null
                        && u.getCreatedAt().isAfter(start)
                        && u.getCreatedAt().isBefore(end)
                        && Role.PLAYER.equals(u.getRole()))  // ← Comparaison safe
                .toList();

        for (User u : eligible) {
            if (promoRepo.existsByCode("WELCOME10_" + u.getId())) continue;

            PromoCode p = new PromoCode();
            p.setCode("WELCOME10_" + u.getId());
            p.setType(DiscountType.PERCENTAGE);
            p.setValue(10.0);
            p.setMaxGlobalUsage(1);
            p.setMaxUsagePerUser(1);
            p.setExpirationDate(LocalDateTime.now().plusDays(7));
            p.setSource("AUTO_WELCOME");
            p.setActive(true);
            promoRepo.save(p);
            log.info("✅ Promo générée: WELCOME10 pour User #{}", u.getId());
        }
    }

    // ========================================================================
    // CAS 2 : PANIER10 - Panier abandonné > 48h
    // ========================================================================
    @Scheduled(cron = "0 0 */2 * * *") // Toutes les 2 heures
    @Transactional
    public void generateAbandonedCartCodes() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(48);

        // Utiliser la méthode repository au lieu de filtrer en mémoire
        List<Panier> abandoned = panierRepo.findByUpdatedAtBefore(cutoff)
                .stream()
                .filter(p -> p.getUser() != null && p.getUser().getRole() == Role.PLAYER)
                .filter(p -> !promoRepo.existsByCode("PANIER10_" + p.getUser().getId()))
                .filter(p -> !usageRepo.existsByUserIdAndPromoCodeSource(
                        p.getUser().getId(), "AUTO_CART_ABANDON"))
                .toList();

        for (Panier panier : abandoned) {
            User u = panier.getUser();
            if (u == null) continue;

            PromoCode p = new PromoCode();
            p.setCode("PANIER10_" + u.getId());
            p.setType(DiscountType.PERCENTAGE);
            p.setValue(10.0);
            p.setMaxGlobalUsage(1);
            p.setMaxUsagePerUser(1);
            p.setExpirationDate(LocalDateTime.now().plusDays(3));
            p.setSource("AUTO_CART_ABANDON");
            p.setActive(true);

            promoRepo.save(p);
            log.info("✅ Promo générée: PANIER10 pour User #{} (panier abandonné)", u.getId());
        }
    }

    // ========================================================================
    // CAS 3 : FIDELITE15 - Après 3 commandes LIVREE
    // ========================================================================
    @Scheduled(cron = "0 0 2 * * *") // Tous les jours à 2h du matin
    @Transactional
    public void generateFidelityCodes() {
        // Trouver les users avec exactement 3 commandes LIVREE
        List<User> players = userRepo.findByRole(Role.PLAYER);

        for (User u : players) {
            long deliveredCount = commandeRepo.countByUserIdAndStatut(u.getId(), CommandeStatus.LIVREE);

            if (deliveredCount == 3) {
                // Vérifier si code déjà généré ou utilisé
                if (promoRepo.existsByCode("FIDELITE15_" + u.getId())) continue;
                if (usageRepo.existsByUserIdAndPromoCodeSource(u.getId(), "AUTO_FIDELITY")) continue;

                PromoCode p = new PromoCode();
                p.setCode("FIDELITE15_" + u.getId());
                p.setType(DiscountType.PERCENTAGE);
                p.setValue(15.0);
                p.setMaxGlobalUsage(1);
                p.setMaxUsagePerUser(1);
                p.setExpirationDate(LocalDateTime.now().plusDays(14));
                p.setSource("AUTO_FIDELITY");
                p.setActive(true);

                promoRepo.save(p);
                log.info("✅ Promo générée: FIDELITE15 pour User #{} (3 commandes livrées)", u.getId());
            }
        }
    }

    // ========================================================================
    // CAS 4 : BIGCART20 - Règle de validation (pas de génération)
    // Ce code est créé MANUELLEMENT une fois en base avec minCartAmount = 200
    // Exemple SQL à exécuter une fois :
    // INSERT INTO promo_code (code, type, value, min_cart_amount, source, active, ...)
    // VALUES ('BIGCART20', 'PERCENTAGE', 20.0, 200.0, 'MANUAL', true, ...);
    // ========================================================================

    // Méthode utilitaire pour créer BIGCART20 manuellement via code (optionnel)
    @Transactional
    public void createBigCartPromoOnce() {
        if (!promoRepo.existsByCode("BIGCART20")) {
            PromoCode p = new PromoCode();
            p.setCode("BIGCART20");
            p.setType(DiscountType.PERCENTAGE);
            p.setValue(20.0);
            p.setMinCartAmount(200.0); // ⚠️ Seuil déclencheur
            p.setMaxGlobalUsage(0); // Illimité
            p.setMaxUsagePerUser(1);
            p.setExpirationDate(LocalDateTime.now().plusYears(1));
            p.setSource("MANUAL");
            p.setActive(true);
            promoRepo.save(p);
            log.info("✅ Promo BIGCART20 créée (seuil 200 TND)");
        }
    }
}
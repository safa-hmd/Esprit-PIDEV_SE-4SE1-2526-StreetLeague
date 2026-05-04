package com.example.streetleague.scheduler;

import com.example.streetleague.Repository.LivraisonRepository;
import com.example.streetleague.ServiceImp.DispatchService;
import com.example.streetleague.domain.Livraison;
import com.example.streetleague.domain.LivraisonStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchScheduler {

    private final LivraisonRepository livraisonRepository;
    private final DispatchService dispatchService;

    // ════════════════════════════════════════════════════
    // PARTIE 7 — Scheduler : toutes les 60 secondes
    // ════════════════════════════════════════════════════
    @Scheduled(fixedRate = 60000)
    public void dispatchLivraisons() {
        List<Livraison> livraisonsEnAttente =
                livraisonRepository.findByStatut(LivraisonStatus.PREPAREE);

        if (livraisonsEnAttente.isEmpty()) {
            log.debug("Scheduler dispatch : aucune livraison PREPAREE.");
            return;
        }

        log.info("Scheduler dispatch : {} livraison(s) à assigner.", livraisonsEnAttente.size());

        for (Livraison livraison : livraisonsEnAttente) {
            dispatchService.autoAssignerLivreur(livraison).ifPresentOrElse(
                    livreur -> log.info("✅ Livraison #{} → {}", livraison.getId(), livreur.getFullName()),
                    ()      -> log.warn("⚠️ Livraison #{} : aucun livreur disponible", livraison.getId())
            );
        }
    }
}
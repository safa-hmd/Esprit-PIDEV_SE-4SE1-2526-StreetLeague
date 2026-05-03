package com.example.streetleague.Service;

import com.example.streetleague.Repository.EvenementCommunauteRepository;
import com.example.streetleague.Repository.SponsoringEvenementRepository;
import com.example.streetleague.Entity.EvenementCommunaute;
import com.example.streetleague.Entity.SponsoringEvenement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SponsoringScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SponsoringScheduler.class);
    private final SponsoringEvenementRepository sponsoringRepository;
    private final EvenementCommunauteRepository evenementRepository;

    public SponsoringScheduler(SponsoringEvenementRepository sponsoringRepository,
                                EvenementCommunauteRepository evenementRepository) {
        this.sponsoringRepository = sponsoringRepository;
        this.evenementRepository = evenementRepository;
    }

    /**
     * Scheduler exécuté tous les jours à 2h du matin pour vérifier les sponsoring expirés
     * et mettre à jour la base de données selon la logique métier
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void verifierSponsoringsExpirés() {
        logger.info("Démarrage scheduler sponsoring - {}", new Date());

        try {
            // 1. Récupérer les événements passés
            List<EvenementCommunaute> evenementsPassés = evenementRepository.findEvenementsPassés(new Date());
            int misesAJour = 0;

            // 2. Mettre à jour les sponsoring expirés
            for (EvenementCommunaute evenement : evenementsPassés) {
                List<SponsoringEvenement> sponsorings = sponsoringRepository.findByEvenementId(evenement.getId());

                for (SponsoringEvenement sponsoring : sponsorings) {
                    if ("ACTIF".equals(sponsoring.getStatut()) || "EN_ATTENTE".equals(sponsoring.getStatut())) {
                        sponsoring.setStatut("EXPIRÉ");
                        sponsoringRepository.save(sponsoring);
                        misesAJour++;
                    }
                }
            }

            // 3. Nettoyer les anciennes données (6 mois)
            Date dateLimite = new Date(System.currentTimeMillis() - (6L * 30 * 24 * 60 * 60 * 1000));
            List<SponsoringEvenement> anciens = sponsoringRepository.findSponsoringsExpirésAvantDate(dateLimite);
            
            if (!anciens.isEmpty()) {
                sponsoringRepository.deleteAll(anciens);
            }

            logger.info("Scheduler terminé - {} mises à jour, {} nettoyées", misesAJour, anciens.size());

        } catch (Exception e) {
            logger.error("Erreur scheduler sponsoring", e);
        }
    }
}

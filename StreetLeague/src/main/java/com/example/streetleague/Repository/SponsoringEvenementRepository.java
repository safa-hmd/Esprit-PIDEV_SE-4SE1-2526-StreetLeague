package com.example.streetleague.Repository;

import com.example.streetleague.Entity.SponsoringEvenement;
import com.example.streetleague.dto.CommunauteStatsDTO;
import com.example.streetleague.dto.DashboardSponsorCommunauteDTO;
import com.example.streetleague.dto.TopCommunauteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface SponsoringEvenementRepository extends JpaRepository<SponsoringEvenement, Long> {

        @Query("SELECT se FROM SponsoringEvenement se WHERE se.evenement.id = :evenementId")
        List<SponsoringEvenement> findByEvenementId(@Param("evenementId") Long evenementId);

        @Query("SELECT se FROM SponsoringEvenement se WHERE se.sponsor.id = :sponsorId")
        List<SponsoringEvenement> findBySponsorId(@Param("sponsorId") Long sponsorId);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("DELETE FROM SponsoringEvenement se WHERE se.sponsor.id = :sponsorId")
        void deleteAllForSponsor(@Param("sponsorId") Long sponsorId);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("DELETE FROM SponsoringEvenement se WHERE se.evenement.id = :evenementId")
        void deleteAllForEvenement(@Param("evenementId") Long evenementId);

        
        // ===== MÉTHODES POUR LE SCHEDULER =====

        // Compter les sponsoring par statut
        long countByStatut(String statut);

        // Trouver les sponsoring expirés avant une date
        @Query("SELECT se FROM SponsoringEvenement se WHERE se.statut = 'EXPIRÉ' AND se.evenement.date < :dateLimite")
        List<SponsoringEvenement> findSponsoringsExpirésAvantDate(@Param("dateLimite") Date dateLimite);

        // ===== MÉTIERS AVANCÉS : JOINTURES 3+ TABLES =====

        /**
         * Requête avancée #1 : Contribution totale par communauté
         * JOIN 3 TABLES : SponsoringEvenement → EvenementCommunaute → Communaute
         * Concepts : JOIN, COUNT(DISTINCT), SUM, COALESCE, GROUP BY, ORDER BY
         */
        @Query("SELECT new com.example.streetleague.dto.CommunauteStatsDTO(" +
                        "c.nom, c.type, COUNT(DISTINCT e.id), COUNT(se.id), COALESCE(SUM(se.contribution), 0)) " +
                        "FROM SponsoringEvenement se " +
                        "JOIN se.evenement e " +
                        "JOIN e.communaute c " +
                        "GROUP BY c.id, c.nom, c.type " +
                        "ORDER BY SUM(se.contribution) DESC")
        List<CommunauteStatsDTO> getContributionTotaleParCommunaute();

        /**
         * Requête avancée #2 : Top communautés avec seuil minimum de sponsorings
         * JOIN 3 TABLES : Communaute → EvenementCommunaute → SponsoringEvenement
         * Concepts : JOIN, HAVING, AVG, MAX, COUNT(DISTINCT), GROUP BY
         */
        @Query("SELECT new com.example.streetleague.dto.TopCommunauteDTO(" +
                        "c.nom, c.type, COUNT(DISTINCT e.id), COUNT(se.id), " +
                        "AVG(se.contribution), MAX(se.contribution)) " +
                        "FROM SponsoringEvenement se " +
                        "JOIN se.evenement e " +
                        "JOIN e.communaute c " +
                        "WHERE (:statut IS NULL OR se.statut = :statut) " +
                        "GROUP BY c.id, c.nom, c.type " +
                        "HAVING COUNT(se.id) >= :seuilMinimum " +
                        "ORDER BY AVG(se.contribution) DESC")
        List<TopCommunauteDTO> getTopCommunautesAvecSponsorings(
                        @Param("statut") String statut,
                        @Param("seuilMinimum") Long seuilMinimum);

        /**
         * Requête avancée #6 : Dashboard complet Sponsor → Communauté
         * JOIN 4 TABLES : Sponsor → SponsoringEvenement → EvenementCommunaute →
         * Communaute
         * Concepts : 4 JOIN chaînées, COUNT(DISTINCT), SUM, AVG, GROUP BY multi-tables
         */
        @Query("SELECT new com.example.streetleague.dto.DashboardSponsorCommunauteDTO(" +
                        "s.nom, s.type, c.nom, c.type, " +
                        "COUNT(DISTINCT e.id), SUM(se.contribution), AVG(se.contribution)) " +
                        "FROM SponsoringEvenement se " +
                        "JOIN se.sponsor s " +
                        "JOIN se.evenement e " +
                        "JOIN e.communaute c " +
                        "WHERE (:statut IS NULL OR se.statut = :statut) " +
                        "GROUP BY s.id, s.nom, s.type, c.id, c.nom, c.type " +
                        "ORDER BY SUM(se.contribution) DESC")
        List<DashboardSponsorCommunauteDTO> getDashboardSponsorParCommunaute(
                        @Param("statut") String statut);
}

package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Sponsor;
import com.example.streetleague.dto.ComparaisonSponsorDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    // Exemple : trouver un sponsor par email
    Optional<Sponsor> findByContactEmail(String email);

    /**
     * Requête avancée #5 : Comparaison contrats vs sponsorings par sponsor
     * 3 TABLES avec sous-requêtes corrélées : Sponsor → ContratSponsor + SponsoringEvenement
     * Concepts : Sous-requêtes corrélées (SELECT dans SELECT), COALESCE, COUNT, SUM
     */
    @Query("SELECT new com.example.streetleague.dto.ComparaisonSponsorDTO(" +
           "s.nom, s.type, " +
           "COALESCE((SELECT SUM(cs.montant) FROM com.example.streetleague.Entity.ContratSponsor cs WHERE cs.sponsor = s), 0), " +
           "COALESCE((SELECT SUM(se.contribution) FROM com.example.streetleague.Entity.SponsoringEvenement se WHERE se.sponsor = s), 0), " +
           "COALESCE((SELECT COUNT(cs.id) FROM com.example.streetleague.Entity.ContratSponsor cs WHERE cs.sponsor = s), 0), " +
           "COALESCE((SELECT COUNT(se.id) FROM com.example.streetleague.Entity.SponsoringEvenement se WHERE se.sponsor = s), 0)) " +
           "FROM Sponsor s " +
           "ORDER BY s.nom")
    List<ComparaisonSponsorDTO> getComparaisonContratsVsSponsorings();
}

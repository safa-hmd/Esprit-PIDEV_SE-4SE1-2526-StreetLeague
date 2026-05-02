package com.example.streetleague.Repository;

import com.example.streetleague.Entity.EvenementCommunaute;
import com.example.streetleague.dto.EvenementSansSponsoringDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EvenementCommunauteRepository extends JpaRepository<EvenementCommunaute, Long> {
    // Exemple : trouver les événements d'une communauté
    List<EvenementCommunaute> findByCommunauteId(Long communauteId);
    
    // Trouver les événements passés (pour le scheduler)
    @Query("SELECT e FROM EvenementCommunaute e WHERE e.date < :dateActuelle")
    List<EvenementCommunaute> findEvenementsPassés(@Param("dateActuelle") Date dateActuelle);

    /**
     * Requête avancée #4 : Événements sans aucun sponsoring
     * LEFT JOIN 3 TABLES : EvenementCommunaute → Communaute + LEFT JOIN SponsoringEvenement
     * Concepts : LEFT JOIN, IS NULL, JOIN, ORDER BY
     * Identifie les événements orphelins qui n'ont reçu aucun sponsoring
     */
    @Query("SELECT new com.example.streetleague.dto.EvenementSansSponsoringDTO(" +
           "c.nom, e.titre, e.date, e.description) " +
           "FROM EvenementCommunaute e " +
           "JOIN e.communaute c " +
           "LEFT JOIN com.example.streetleague.Entity.SponsoringEvenement se ON se.evenement = e " +
           "WHERE se.id IS NULL " +
           "ORDER BY c.nom, e.date DESC")
    List<EvenementSansSponsoringDTO> getEvenementsSansSponsoring();

    /**
     * Requête avancée #4b : Événements sans sponsoring après une date donnée
     * LEFT JOIN 3 TABLES avec filtre de date
     * Concepts : LEFT JOIN, IS NULL, JOIN, WHERE, ORDER BY
     */
    @Query("SELECT new com.example.streetleague.dto.EvenementSansSponsoringDTO(" +
           "c.nom, e.titre, e.date, e.description) " +
           "FROM EvenementCommunaute e " +
           "JOIN e.communaute c " +
           "LEFT JOIN com.example.streetleague.Entity.SponsoringEvenement se ON se.evenement = e " +
           "WHERE se.id IS NULL " +
           "AND e.date > :dateDebut " +
           "ORDER BY c.nom, e.date DESC")
    List<EvenementSansSponsoringDTO> getEvenementsSansSponsoringApresDate(
        @Param("dateDebut") Date dateDebut);
}


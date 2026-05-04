package com.example.streetleague.Repository;

import com.example.streetleague.domain.Livraison;
import com.example.streetleague.domain.LivraisonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    // Livraisons par statut (pour le dispatcher)
    List<Livraison> findByStatut(LivraisonStatus statut);

    // ✅ CORRIGÉ : livreur est @ManyToOne → navigation via livreur.id avec underscore
    List<Livraison> findByLivreur_Id(Long livreurId);

    // Stats
    long countByStatut(LivraisonStatus statut);

    List<Livraison> findByLivreur_IdAndStatutIn(Long livreurId, List<LivraisonStatus> statuts);
    List<Livraison> findByDateCreationAfter(LocalDateTime debut);

    @Query("""
    SELECT l.livreur.id,
           l.livreur.fullName,
           COUNT(l),
           SUM(CASE WHEN l.statut = 'LIVREE' THEN 1 ELSE 0 END),
           AVG(FUNCTION('TIMESTAMPDIFF', HOUR, l.dateAffectation, l.dateLivraison)),
           AVG(l.scoreAffectation)
    FROM Livraison l
    WHERE l.livreur IS NOT NULL
      AND l.dateCreation >= :debut
    GROUP BY l.livreur.id, l.livreur.fullName
    ORDER BY COUNT(l) DESC
    """)
    List<Object[]> perfParLivreur(@Param("debut") LocalDateTime debut);
}
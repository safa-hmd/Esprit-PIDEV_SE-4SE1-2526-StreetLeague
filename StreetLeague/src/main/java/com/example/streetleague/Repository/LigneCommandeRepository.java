package com.example.streetleague.Repository;

import com.example.streetleague.domain.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    @Query(value = """
        SELECT m.id,
               m.nom,
               SUM(lc.quantite) AS qte_vendue,
               SUM(lc.quantite * lc.prix_unitaire) AS ca_genere,
               m.quantite_stock
        FROM ligne_commande lc
        JOIN materiels m ON m.id = lc.materiel_id
        JOIN commandes c ON c.id = lc.commande_id
        WHERE c.date_creation >= :debut
        GROUP BY m.id, m.nom, m.quantite_stock
        ORDER BY qte_vendue DESC
        LIMIT :topN
    """, nativeQuery = true)
    List<Object[]> topProduits(@Param("debut") LocalDateTime debut,
                               @Param("topN") int topN);
}
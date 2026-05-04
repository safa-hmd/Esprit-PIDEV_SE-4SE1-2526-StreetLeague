package com.example.streetleague.Repository;

import com.example.streetleague.domain.Commande;
import com.example.streetleague.domain.CommandeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    long countByUserIdAndStatut(Long userId, CommandeStatus statut);

    @Query(value = "SELECT COALESCE(SUM(c.montant_total),0) FROM commandes c WHERE c.date_creation BETWEEN :debut AND :fin", nativeQuery = true)
    Double sumCaByPeriode(@Param("debut") LocalDateTime debut,
                          @Param("fin") LocalDateTime fin);

    @Query(value = "SELECT DATE(c.date_creation), SUM(c.montant_total), COUNT(*) FROM commandes c WHERE c.date_creation >= :debut GROUP BY DATE(c.date_creation) ORDER BY DATE(c.date_creation)", nativeQuery = true)
    List<Object[]> caParJour(@Param("debut") LocalDateTime debut);

    @Query(value = "SELECT COUNT(*) FROM commandes c WHERE c.date_creation >= :debut", nativeQuery = true)
    Long countByDateCreationAfter(@Param("debut") LocalDateTime debut);

}
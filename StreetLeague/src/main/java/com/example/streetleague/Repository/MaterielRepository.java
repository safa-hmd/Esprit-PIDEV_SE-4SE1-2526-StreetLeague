package com.example.streetleague.Repository;

import com.example.streetleague.domain.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterielRepository extends JpaRepository<Materiel, Long> {
    Long countByQuantiteStockLessThan(int seuil);

}
package com.example.streetleague.Repository;

import com.example.streetleague.domain.EvenementCommunaute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvenementCommunauteRepository extends JpaRepository<EvenementCommunaute, Long> {
    // Exemple : trouver les événements d'une communauté
    List<EvenementCommunaute> findByCommunauteId(Long communauteId);
}


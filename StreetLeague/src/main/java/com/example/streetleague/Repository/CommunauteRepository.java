package com.example.streetleague.Repository;

import com.example.streetleague.Entity.Communaute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunauteRepository extends JpaRepository<Communaute, Long> {
    // Exemple de méthode personnalisée
    List<Communaute> findByNomContainingIgnoreCase(String nom);
}

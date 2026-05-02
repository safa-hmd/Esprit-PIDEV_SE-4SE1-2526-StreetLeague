package com.example.streetleague.Repository;

import com.example.streetleague.domain.Panier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUserIdUser(Long idUser);


}
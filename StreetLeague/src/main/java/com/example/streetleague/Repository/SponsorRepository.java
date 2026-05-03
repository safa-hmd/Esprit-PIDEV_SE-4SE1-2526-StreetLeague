package com.example.streetleague.Repository;

import com.example.streetleague.domain.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    // Exemple : trouver un sponsor par email
    Optional<Sponsor> findByContactEmail(String email);
}


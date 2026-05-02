package com.example.streetleague.Repository;

import com.example.streetleague.domain.Transporteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransporteurRepository extends JpaRepository<Transporteur, Long> {
}
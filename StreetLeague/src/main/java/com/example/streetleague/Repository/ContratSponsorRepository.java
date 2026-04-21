package com.example.streetleague.Repository;

import com.example.streetleague.domain.ContratSponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratSponsorRepository extends JpaRepository<ContratSponsor, Long> {
    List<ContratSponsor> findByStatut(String statut);

    @Query("SELECT c FROM ContratSponsor c WHERE c.sponsor.id = :sponsorId")
    List<ContratSponsor> findBySponsorId(@Param("sponsorId") Long sponsorId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM ContratSponsor c WHERE c.sponsor.id = :sponsorId")
    void deleteAllForSponsor(@Param("sponsorId") Long sponsorId);
}

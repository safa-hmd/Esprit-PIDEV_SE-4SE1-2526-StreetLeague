package com.example.streetleague.Repository;

import com.example.streetleague.domain.SponsoringEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SponsoringEvenementRepository extends JpaRepository<SponsoringEvenement, Long> {

    @Query("SELECT se FROM SponsoringEvenement se WHERE se.evenement.id = :evenementId")
    List<SponsoringEvenement> findByEvenementId(@Param("evenementId") Long evenementId);

    @Query("SELECT se FROM SponsoringEvenement se WHERE se.sponsor.id = :sponsorId")
    List<SponsoringEvenement> findBySponsorId(@Param("sponsorId") Long sponsorId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM SponsoringEvenement se WHERE se.sponsor.id = :sponsorId")
    void deleteAllForSponsor(@Param("sponsorId") Long sponsorId);
}

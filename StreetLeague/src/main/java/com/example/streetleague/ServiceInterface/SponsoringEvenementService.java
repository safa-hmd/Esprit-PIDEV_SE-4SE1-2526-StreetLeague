package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.CommunauteStatsDTO;
import com.example.streetleague.dto.DashboardSponsorCommunauteDTO;
import com.example.streetleague.dto.SponsoringEvenementDTO;
import com.example.streetleague.dto.TopCommunauteDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface SponsoringEvenementService {
    SponsoringEvenementDTO create(SponsoringEvenementDTO dto);
    List<SponsoringEvenementDTO> getAll();
    SponsoringEvenementDTO getById(Long id);
    SponsoringEvenementDTO update(Long id, SponsoringEvenementDTO dto);
    void delete(Long id);
    SponsoringEvenementDTO updateStatus(Long id, String statut);
    
        
    // ===== MÉTIERS AVANCÉS : JOINTURES 3+ TABLES =====
    
    /** Requête #1 : Contribution totale par communauté (3 tables) */
    List<CommunauteStatsDTO> getContributionTotaleParCommunaute();
    
    /** Requête #2 : Top communautés avec seuil (3 tables + HAVING) */
    List<TopCommunauteDTO> getTopCommunautesAvecSponsorings(String statut, Long seuilMinimum);
    
    /** Requête #6 : Dashboard sponsor-communauté (4 tables) */
    List<DashboardSponsorCommunauteDTO> getDashboardSponsorParCommunaute(String statut);
}

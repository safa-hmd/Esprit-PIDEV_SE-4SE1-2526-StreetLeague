package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.ComparaisonSponsorDTO;
import com.example.streetleague.dto.SponsorDTO;

import java.util.List;

public interface SponsorService {
    SponsorDTO create(SponsorDTO dto);
    List<SponsorDTO> getAll();
    SponsorDTO getById(Long id);
    SponsorDTO update(Long id, SponsorDTO dto);
    void delete(Long id);
    SponsorDTO updateStatus(Long id, String statut);
    
    // ===== MÉTIER AVANCÉ : SOUS-REQUÊTES CORRÉLÉES 3 TABLES =====
    
    /** Requête #5 : Comparaison contrats vs sponsorings (sous-requêtes corrélées) */
    List<ComparaisonSponsorDTO> getComparaisonContratsVsSponsorings();
}

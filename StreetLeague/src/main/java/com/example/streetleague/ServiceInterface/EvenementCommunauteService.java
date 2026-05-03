package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.EvenementCommunauteDTO;
import com.example.streetleague.dto.EvenementSansSponsoringDTO;

import java.util.Date;
import java.util.List;

public interface EvenementCommunauteService {
    EvenementCommunauteDTO create(EvenementCommunauteDTO dto);
    List<EvenementCommunauteDTO> getAll();
    EvenementCommunauteDTO getById(Long id);
    EvenementCommunauteDTO update(Long id, EvenementCommunauteDTO dto);
    void delete(Long id);
    
    // ===== MÉTIER AVANCÉ : LEFT JOIN 3 TABLES =====
    
    /** Requête #4 : Événements sans sponsoring (LEFT JOIN) */
    List<EvenementSansSponsoringDTO> getEvenementsSansSponsoring();
    
    /** Requête #4b : Événements sans sponsoring après une date */
    List<EvenementSansSponsoringDTO> getEvenementsSansSponsoringApresDate(Date dateDebut);
}

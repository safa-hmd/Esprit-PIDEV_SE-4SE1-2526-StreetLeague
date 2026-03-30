package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.ContratSponsorDTO;

import java.util.List;

public interface ContratSponsorService {
    ContratSponsorDTO create(ContratSponsorDTO dto);
    List<ContratSponsorDTO> getAll();
    ContratSponsorDTO getById(Long id);
    ContratSponsorDTO update(Long id, ContratSponsorDTO dto);
    void delete(Long id);
}

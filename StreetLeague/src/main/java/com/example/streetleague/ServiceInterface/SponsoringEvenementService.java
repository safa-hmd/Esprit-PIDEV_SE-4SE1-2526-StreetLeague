package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.SponsoringEvenementDTO;

import java.util.List;

public interface SponsoringEvenementService {
    SponsoringEvenementDTO create(SponsoringEvenementDTO dto);
    List<SponsoringEvenementDTO> getAll();
    SponsoringEvenementDTO getById(Long id);
    SponsoringEvenementDTO update(Long id, SponsoringEvenementDTO dto);
    void delete(Long id);
}

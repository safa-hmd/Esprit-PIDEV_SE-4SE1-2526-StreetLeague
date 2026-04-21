package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.SponsorDTO;

import java.util.List;

public interface SponsorService {
    SponsorDTO create(SponsorDTO dto);
    List<SponsorDTO> getAll();
    SponsorDTO getById(Long id);
    SponsorDTO update(Long id, SponsorDTO dto);
    void delete(Long id);
}

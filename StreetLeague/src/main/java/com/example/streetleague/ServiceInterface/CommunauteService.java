package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.CommunauteDTO;

import java.util.List;

public interface CommunauteService {
    CommunauteDTO create(CommunauteDTO dto);
    List<CommunauteDTO> getAll();
    CommunauteDTO getById(Long id);
    CommunauteDTO update(Long id, CommunauteDTO dto);
    void delete(Long id);
}


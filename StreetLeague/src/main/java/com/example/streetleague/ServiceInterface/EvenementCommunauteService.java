package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.EvenementCommunauteDTO;

import java.util.List;

public interface EvenementCommunauteService {
    EvenementCommunauteDTO create(EvenementCommunauteDTO dto);
    List<EvenementCommunauteDTO> getAll();
    EvenementCommunauteDTO getById(Long id);
    EvenementCommunauteDTO update(Long id, EvenementCommunauteDTO dto);
    void delete(Long id);
}

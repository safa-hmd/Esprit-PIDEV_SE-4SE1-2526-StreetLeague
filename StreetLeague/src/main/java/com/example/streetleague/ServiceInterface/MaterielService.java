package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.MaterielDTO;

import java.util.List;

public interface MaterielService {

    MaterielDTO create(MaterielDTO dto);

    MaterielDTO update(Long id, MaterielDTO dto);

    void delete(Long id);

    MaterielDTO getById(Long id);

    List<MaterielDTO> getAll();
}
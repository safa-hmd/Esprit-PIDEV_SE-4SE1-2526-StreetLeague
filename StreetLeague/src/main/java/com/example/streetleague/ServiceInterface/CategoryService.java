package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO create(CategoryDTO dto);

    CategoryDTO update(Long id, CategoryDTO dto);

    void delete(Long id);

    CategoryDTO getById(Long id);

    List<CategoryDTO> getAll();
}
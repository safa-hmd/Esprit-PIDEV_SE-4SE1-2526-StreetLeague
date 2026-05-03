package com.example.streetleague.ServiceInterface;

import com.example.streetleague.dto.CommunauteDTO;
import java.util.List;

public interface CommunauteService {
    List<CommunauteDTO> getAll();
    CommunauteDTO getById(Long id);
    CommunauteDTO create(@jakarta.validation.Valid CommunauteDTO communauteDTO);
    CommunauteDTO update(Long id, @jakarta.validation.Valid CommunauteDTO communauteDTO);
    void delete(Long id);

    // Default implementations for backward compatibility if needed elsewhere
    default List<CommunauteDTO> getAllCommunautes() { return getAll(); }
    default CommunauteDTO getCommunauteById(Long id) { return getById(id); }
    default CommunauteDTO createCommunaute(CommunauteDTO dto) { return create(dto); }
    default CommunauteDTO updateCommunaute(Long id, CommunauteDTO dto) { return update(id, dto); }
    default void deleteCommunaute(Long id) { delete(id); }
    default List<CommunauteDTO> getCommunautesByCreateur(Long createurId) { return List.of(); }
    default List<CommunauteDTO> searchCommunautes(String keyword) { return List.of(); }
}

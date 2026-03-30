package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.CommunauteRepository;
import com.example.streetleague.ServiceInterface.CommunauteService;
import com.example.streetleague.domain.Communaute;
import com.example.streetleague.dto.CommunauteDTO;
import com.example.streetleague.mapper.CommunauteMapper;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunauteServiceImp implements CommunauteService {
    private final CommunauteRepository repo;
    private final CommunauteMapper mapper;

    public CommunauteServiceImp(CommunauteRepository repo, CommunauteMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CommunauteDTO create(CommunauteDTO dto) {
        Communaute entity = mapper.toEntity(dto);
        return mapper.toDTO(repo.save(entity));
    }

    @Override
    public List<CommunauteDTO> getAll() {
        return repo.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public CommunauteDTO getById(Long id) {
        return repo.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Communauté introuvable"));
    }

    @Override
    public CommunauteDTO update(Long id, CommunauteDTO dto) {
        Communaute entity = repo.findById(id).orElseThrow(() -> new RuntimeException("Communauté introuvable"));
        Communaute updated = mapper.toEntity(dto);
        updated.setId(entity.getId());
        return mapper.toDTO(repo.save(updated));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}

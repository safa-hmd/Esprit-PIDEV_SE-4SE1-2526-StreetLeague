package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.ContratSponsorRepository;
import com.example.streetleague.Repository.SponsorRepository;
import com.example.streetleague.Repository.SponsoringEvenementRepository;
import com.example.streetleague.ServiceInterface.SponsorService;
import com.example.streetleague.Entity.Sponsor;
import com.example.streetleague.dto.ComparaisonSponsorDTO;
import com.example.streetleague.dto.SponsorDTO;
import com.example.streetleague.mapper.SponsorMapper;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SponsorServiceImp implements SponsorService {
    private final SponsorRepository repo;
    private final SponsorMapper mapper;
    private final SponsoringEvenementRepository sponsoringEvenementRepository;
    private final ContratSponsorRepository contratSponsorRepository;

    public SponsorServiceImp(SponsorRepository repo,
                              SponsorMapper mapper,
                              SponsoringEvenementRepository sponsoringEvenementRepository,
                              ContratSponsorRepository contratSponsorRepository) {
        this.repo = repo;
        this.mapper = mapper;
        this.sponsoringEvenementRepository = sponsoringEvenementRepository;
        this.contratSponsorRepository = contratSponsorRepository;
    }

    @Override
    public SponsorDTO create(SponsorDTO dto) {
        Sponsor entity = mapper.toEntity(dto);
        return mapper.toDTO(repo.save(entity));
    }

    @Override
    public List<SponsorDTO> getAll() {
        return repo.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public SponsorDTO getById(Long id) {
        return repo.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sponsor introuvable"));
    }

    @Override
    public SponsorDTO update(Long id, SponsorDTO dto) {
        Sponsor entity = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Sponsor introuvable"));
        Sponsor updated = mapper.toEntity(dto);
        updated.setId(entity.getId());
        return mapper.toDTO(repo.save(updated));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Sponsor sponsor = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sponsor introuvable avec l'ID : " + id));

        sponsoringEvenementRepository.deleteAllForSponsor(id);
        contratSponsorRepository.deleteAllForSponsor(id);
        repo.delete(sponsor);
    }

    @Override
    @Transactional
    public SponsorDTO updateStatus(Long id, String statut) {
        Sponsor sponsor = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sponsor introuvable avec l'ID : " + id));
        
        // Si rejeté, supprimer automatiquement
        if ("REJETÉ".equals(statut)) {
            // Supprimer les relations d'abord
            sponsoringEvenementRepository.deleteAllForSponsor(id);
            contratSponsorRepository.deleteAllForSponsor(id);
            // Supprimer le sponsor
            repo.delete(sponsor);
            return null; // Indiquer que l'élément a été supprimé
        }
        
        // Si approuvé, mettre à jour le statut
        sponsor.setStatut(statut);
        Sponsor updated = repo.save(sponsor);
        return mapper.toDTO(updated);
    }

    // ===== MÉTIER AVANCÉ : SOUS-REQUÊTES CORRÉLÉES 3 TABLES =====
    
    @Override
    public List<ComparaisonSponsorDTO> getComparaisonContratsVsSponsorings() {
        return repo.getComparaisonContratsVsSponsorings();
    }
}

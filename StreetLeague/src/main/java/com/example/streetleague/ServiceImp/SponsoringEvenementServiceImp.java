package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.EvenementCommunauteRepository;
import com.example.streetleague.Repository.SponsorRepository;
import com.example.streetleague.Repository.SponsoringEvenementRepository;
import com.example.streetleague.ServiceInterface.SponsoringEvenementService;
import com.example.streetleague.Entity.EvenementCommunaute;
import com.example.streetleague.Entity.Sponsor;
import com.example.streetleague.Entity.SponsoringEvenement;
import com.example.streetleague.dto.CommunauteStatsDTO;
import com.example.streetleague.dto.DashboardSponsorCommunauteDTO;
import com.example.streetleague.dto.SponsoringEvenementDTO;
import com.example.streetleague.dto.TopCommunauteDTO;
import com.example.streetleague.mapper.SponsoringEvenementMapper;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SponsoringEvenementServiceImp implements SponsoringEvenementService {
    private final SponsoringEvenementRepository repo;
    private final SponsoringEvenementMapper mapper;
    private final SponsorRepository sponsorRepository;
    private final EvenementCommunauteRepository evenementRepository;

    public SponsoringEvenementServiceImp(SponsoringEvenementRepository repo,
                                          SponsoringEvenementMapper mapper,
                                          SponsorRepository sponsorRepository,
                                          EvenementCommunauteRepository evenementRepository) {
        this.repo = repo;
        this.mapper = mapper;
        this.sponsorRepository = sponsorRepository;
        this.evenementRepository = evenementRepository;
    }

    @Override
    public SponsoringEvenementDTO create(SponsoringEvenementDTO dto) {
        Sponsor sponsor = sponsorRepository.findById(dto.sponsorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sponsor introuvable avec l'ID : " + dto.sponsorId()));
        EvenementCommunaute evenement = evenementRepository.findById(dto.evenementId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Événement introuvable avec l'ID : " + dto.evenementId()));

        SponsoringEvenement entity = new SponsoringEvenement();
        entity.setSponsor(sponsor);
        entity.setEvenement(evenement);
        entity.setContribution(dto.contribution());
        entity.setTypeContribution(dto.typeContribution());

        return mapper.toDTO(repo.save(entity));
    }

    @Override
    public List<SponsoringEvenementDTO> getAll() {
        return repo.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public SponsoringEvenementDTO getById(Long id) {
        return repo.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sponsoring introuvable"));
    }

    @Override
    public SponsoringEvenementDTO update(Long id, SponsoringEvenementDTO dto) {
        SponsoringEvenement entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sponsoring introuvable"));

        Sponsor sponsor = sponsorRepository.findById(dto.sponsorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sponsor introuvable avec l'ID : " + dto.sponsorId()));
        EvenementCommunaute evenement = evenementRepository.findById(dto.evenementId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Événement introuvable avec l'ID : " + dto.evenementId()));

        entity.setSponsor(sponsor);
        entity.setEvenement(evenement);
        entity.setContribution(dto.contribution());
        entity.setTypeContribution(dto.typeContribution());

        return mapper.toDTO(repo.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SponsoringEvenement entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sponsoring introuvable"));
        repo.delete(entity);
    }

    @Override
    @Transactional
    public SponsoringEvenementDTO updateStatus(Long id, String statut) {
        SponsoringEvenement entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sponsoring introuvable avec l'ID : " + id));
        
        // Si rejeté, supprimer automatiquement
        if ("REJETÉ".equals(statut)) {
            repo.delete(entity);
            return null; // Indiquer que l'élément a été supprimé
        }
        
        // Si approuvé, mettre à jour le statut
        entity.setStatut(statut);
        SponsoringEvenement updated = repo.save(entity);
        return mapper.toDTO(updated);
    }

    
    // ===== MÉTIERS AVANCÉS : JOINTURES 3+ TABLES =====
    
    @Override
    public List<CommunauteStatsDTO> getContributionTotaleParCommunaute() {
        return repo.getContributionTotaleParCommunaute();
    }

    @Override
    public List<TopCommunauteDTO> getTopCommunautesAvecSponsorings(String statut, Long seuilMinimum) {
        return repo.getTopCommunautesAvecSponsorings(statut, seuilMinimum);
    }

    @Override
    public List<DashboardSponsorCommunauteDTO> getDashboardSponsorParCommunaute(String statut) {
        return repo.getDashboardSponsorParCommunaute(statut);
    }
}

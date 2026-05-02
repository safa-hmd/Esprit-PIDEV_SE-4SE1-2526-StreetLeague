package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.ContratSponsorRepository;
import com.example.streetleague.Repository.SponsorRepository;
import com.example.streetleague.ServiceInterface.ContratSponsorService;
import com.example.streetleague.Entity.ContratSponsor;
import com.example.streetleague.Entity.Sponsor;
import com.example.streetleague.dto.ContratSponsorDTO;
import com.example.streetleague.mapper.ContratSponsorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContratSponsorServiceImp implements ContratSponsorService {
    private final ContratSponsorRepository repo;
    private final ContratSponsorMapper mapper;
    private final SponsorRepository sponsorRepository;

    public ContratSponsorServiceImp(ContratSponsorRepository repo,
                                    ContratSponsorMapper mapper,
                                    SponsorRepository sponsorRepository) {
        this.repo = repo;
        this.mapper = mapper;
        this.sponsorRepository = sponsorRepository;
    }

    @Override
    @Transactional
    public ContratSponsorDTO create(ContratSponsorDTO dto) {
        Sponsor sponsor = sponsorRepository.findById(dto.sponsorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sponsor introuvable avec l'ID : " + dto.sponsorId()));

        ContratSponsor entity = new ContratSponsor();
        entity.setSponsor(sponsor);
        entity.setEquipeId(dto.equipeId());
        entity.setMontant(dto.montant());
        entity.setDateDebut(dto.dateDebut());
        entity.setDateFin(dto.dateFin());
        entity.setStatut(dto.statut());
        entity.setConditions(normalizeConditions(dto.conditions()));
        if (entity.getDateFin().before(entity.getDateDebut())) {
            throw new IllegalArgumentException(
                    "La date de fin doit être postérieure ou égale à la date de début");
        }

        return mapper.toDTO(repo.save(entity));
    }

    @Override
    public List<ContratSponsorDTO> getAll() {
        return repo.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public ContratSponsorDTO getById(Long id) {
        return repo.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
    }

    @Override
    @Transactional
    public ContratSponsorDTO update(Long id, ContratSponsorDTO dto) {
        ContratSponsor entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));

        Sponsor sponsor = sponsorRepository.findById(dto.sponsorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sponsor introuvable avec l'ID : " + dto.sponsorId()));

        entity.setSponsor(sponsor);
        entity.setEquipeId(dto.equipeId());
        entity.setMontant(dto.montant());
        entity.setDateDebut(dto.dateDebut());
        entity.setDateFin(dto.dateFin());
        entity.setStatut(dto.statut());
        entity.setConditions(normalizeConditions(dto.conditions()));
        if (entity.getDateFin().before(entity.getDateDebut())) {
            throw new IllegalArgumentException(
                    "La date de fin doit être postérieure ou égale à la date de début");
        }

        return mapper.toDTO(repo.save(entity));
    }

    private static String normalizeConditions(String conditions) {
        return conditions != null && !conditions.isBlank() ? conditions : "Non spécifié";
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    @Transactional
    public ContratSponsorDTO updateStatus(Long id, String statut) {
        ContratSponsor entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Contrat introuvable avec l'ID : " + id));
        
        // Si rejeté, supprimer automatiquement
        if ("REJETÉ".equals(statut)) {
            repo.delete(entity);
            return null; // Indiquer que l'élément a été supprimé
        }
        
        // Si approuvé, mettre à jour le statut
        entity.setStatut(statut);
        ContratSponsor updated = repo.save(entity);
        return mapper.toDTO(updated);
    }
}

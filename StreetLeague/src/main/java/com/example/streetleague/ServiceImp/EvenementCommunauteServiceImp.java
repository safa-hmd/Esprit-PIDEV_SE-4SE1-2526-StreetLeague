package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.CommunauteRepository;
import com.example.streetleague.Repository.EvenementCommunauteRepository;
import com.example.streetleague.Repository.SponsoringEvenementRepository;
import com.example.streetleague.ServiceInterface.EvenementCommunauteService;
import com.example.streetleague.Entity.Communaute;
import com.example.streetleague.Entity.EvenementCommunaute;
import com.example.streetleague.Entity.SponsoringEvenement;
import com.example.streetleague.dto.EvenementCommunauteDTO;
import com.example.streetleague.dto.EvenementSansSponsoringDTO;
import com.example.streetleague.mapper.EvenementCommunauteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvenementCommunauteServiceImp implements EvenementCommunauteService {
    private final EvenementCommunauteRepository repo;
    private final EvenementCommunauteMapper mapper;
    private final CommunauteRepository communauteRepository;
    private final SponsoringEvenementRepository sponsoringEvenementRepository;

    public EvenementCommunauteServiceImp(EvenementCommunauteRepository repo,
                                         EvenementCommunauteMapper mapper,
                                         CommunauteRepository communauteRepository,
                                         SponsoringEvenementRepository sponsoringEvenementRepository) {
        this.repo = repo;
        this.mapper = mapper;
        this.communauteRepository = communauteRepository;
        this.sponsoringEvenementRepository = sponsoringEvenementRepository;
    }

    @Override
    public EvenementCommunauteDTO create(EvenementCommunauteDTO dto) {
        Communaute communaute = communauteRepository.findById(dto.communauteId())
                .orElseThrow(() -> new RuntimeException("Communauté introuvable avec l'ID : " + dto.communauteId()));

        EvenementCommunaute entity = new EvenementCommunaute();
        entity.setCommunaute(communaute);
        entity.setTitre(dto.titre());
        entity.setDescription(dto.description());
        entity.setDate(dto.date());
        entity.setOrganisateurId(dto.organisateurId());

        return mapper.toDTO(repo.save(entity));
    }

    @Override
    public List<EvenementCommunauteDTO> getAll() {
        return repo.findAll().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public EvenementCommunauteDTO getById(Long id) {
        return repo.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));
    }

    @Override
    public EvenementCommunauteDTO update(Long id, EvenementCommunauteDTO dto) {
        EvenementCommunaute entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));

        Communaute communaute = communauteRepository.findById(dto.communauteId())
                .orElseThrow(() -> new RuntimeException("Communauté introuvable avec l'ID : " + dto.communauteId()));

        entity.setCommunaute(communaute);
        entity.setTitre(dto.titre());
        entity.setDescription(dto.description());
        entity.setDate(dto.date());
        entity.setOrganisateurId(dto.organisateurId());

        return mapper.toDTO(repo.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Supprimer d'abord les sponsorings liés (clé étrangère vers evenement_id)
        sponsoringEvenementRepository.deleteAllForEvenement(id);

        repo.deleteById(id);
    }

    // ===== MÉTIER AVANCÉ : LEFT JOIN 3 TABLES =====
    
    @Override
    public List<EvenementSansSponsoringDTO> getEvenementsSansSponsoring() {
        return repo.getEvenementsSansSponsoring();
    }

    @Override
    public List<EvenementSansSponsoringDTO> getEvenementsSansSponsoringApresDate(java.util.Date dateDebut) {
        return repo.getEvenementsSansSponsoringApresDate(dateDebut);
    }
}

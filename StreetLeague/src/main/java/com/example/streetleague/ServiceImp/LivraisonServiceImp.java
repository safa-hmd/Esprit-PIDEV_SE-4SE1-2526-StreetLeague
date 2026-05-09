package com.example.streetleague.ServiceImp;

import com.example.streetleague.ServiceInterface.LivraisonService;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.LivraisonDTO;
import com.example.streetleague.Repository.CommandeRepository;
import com.example.streetleague.Repository.LivraisonRepository;
import com.example.streetleague.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivraisonServiceImp implements LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository  commandeRepository;
    private final UserRepository      userRepository;

    @Override
    public com.example.streetleague.domain.Livraison createLivraison(@Valid LivraisonDTO dto) {

        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande introuvable: " + dto.getCommandeId()));

        // Livreur optionnel — le dispatcher l'assignera automatiquement
        User livreur = null;
        if (dto.getLivreurId() != null) {
            livreur = userRepository.findById(dto.getLivreurId())
                    .orElse(null); // ne pas planter si livreur introuvable
        }

        com.example.streetleague.domain.Livraison livraison =
                com.example.streetleague.domain.Livraison.builder()
                        .commande(commande)
                        .livreur(livreur)
                        .adresse(dto.getAdresse() != null ? dto.getAdresse() : "")
                        .latitudeClient(dto.getLatitudeClient()  != null ? dto.getLatitudeClient()  : 0.0)
                        .longitudeClient(dto.getLongitudeClient() != null ? dto.getLongitudeClient() : 0.0)
                        .fraisLivraison(dto.getFraisLivraison())
                        .statut(dto.getStatut() != null ? dto.getStatut() : LivraisonStatus.PREPAREE)
                        .priorite(dto.getPriorite() != null ? dto.getPriorite() : Priorite.NORMAL)
                        .dateCreation(LocalDateTime.now())
                        .build();

        return livraisonRepository.save(livraison);
    }

    @Transactional
    @Override
    public com.example.streetleague.domain.Livraison updateLivraisonStatus(Long id, @Valid LivraisonDTO dto) {
        com.example.streetleague.domain.Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));

        livraison.setStatut(dto.getStatut());

        if (dto.getStatut() == LivraisonStatus.LIVREE) {
            livraison.setDateLivraison(LocalDateTime.now());
            if (livraison.getLivreur() != null) {
                User livreur = livraison.getLivreur();
                int enCours = Math.max(0, livreur.getLivraisonsEnCours() - 1);
                livreur.setLivraisonsEnCours(enCours);
                if (enCours == 0) livreur.setStatusLivreur(LivreurStatus.DISPONIBLE);
                userRepository.save(livreur);
            }
        }

        if (dto.getMotifEchec() != null && !dto.getMotifEchec().isBlank()) {
            livraison.setMotifEchec(dto.getMotifEchec());
            livraison.setNbTentatives(livraison.getNbTentatives() + 1);
        }

        return livraisonRepository.save(livraison);
    }

    @Transactional
    @Override
    public com.example.streetleague.domain.Livraison getLivraisonById(Long id) {
        return livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));
    }

    @Transactional
    @Override
    public List<com.example.streetleague.domain.Livraison> getAllLivraisons() {
        return livraisonRepository.findAll();
    }

    @Override
    public List<com.example.streetleague.domain.Livraison> getLivraisonsByStatut(LivraisonStatus statut) {
        return livraisonRepository.findByStatut(statut);
    }

    @Override
    public List<com.example.streetleague.domain.Livraison> getLivraisonsByLivreur(Long livreurId) {
        return livraisonRepository.findByLivreur_IdUser(livreurId);
    }
}
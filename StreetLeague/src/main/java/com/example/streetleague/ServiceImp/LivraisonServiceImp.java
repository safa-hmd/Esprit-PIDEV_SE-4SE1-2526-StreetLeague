package com.example.streetleague.ServiceImp;

import com.example.streetleague.ServiceInterface.LivraisonService;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.LivraisonDTO;
import com.example.streetleague.Repository.CommandeRepository;
import com.example.streetleague.Repository.LivraisonRepository;
import com.example.streetleague.Repository.TransporteurRepository;
import com.example.streetleague.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivraisonServiceImp implements LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;
    private final TransporteurRepository transporteurRepository;
    private final UserRepository userRepository;

    @Override
    public Livraison createLivraison(@Valid LivraisonDTO dto) {
        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));
        Transporteur transporteur = transporteurRepository.findById(dto.getTransporteurId())
                .orElseThrow(() -> new RuntimeException("Transporteur introuvable"));
        User livreur = null;
        if (dto.getLivreurId() != null) {
            livreur = userRepository.findById(dto.getLivreurId())
                    .orElseThrow(() -> new RuntimeException("Livreur introuvable"));
        }

        Livraison livraison = Livraison.builder()
                .commande(commande)
                .transporteur(transporteur)
                .livreur(livreur)
                .adresse(dto.getAdresse())
                .fraisLivraison(dto.getFraisLivraison())
                .statut(dto.getStatut())
                .build();

        return livraisonRepository.save(livraison);
    }

    @Transactional
    @Override
    public Livraison updateLivraisonStatus(Long id, @Valid LivraisonDTO dto) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));
        livraison.setStatut(dto.getStatut());
        return livraisonRepository.save(livraison);
    }

    @Transactional
    @Override
    public Livraison getLivraisonById(Long id) {
        return livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));
    }

    @Transactional
    @Override
    public List<Livraison> getAllLivraisons() {
        return livraisonRepository.findAll();
    }
}
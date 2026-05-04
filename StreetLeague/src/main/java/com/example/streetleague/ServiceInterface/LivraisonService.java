package com.example.streetleague.ServiceInterface;

import com.example.streetleague.domain.Livraison;
import com.example.streetleague.domain.LivraisonStatus;
import com.example.streetleague.dto.LivraisonDTO;

import java.util.List;

public interface LivraisonService {
    Livraison createLivraison(LivraisonDTO dto);
    Livraison updateLivraisonStatus(Long id, LivraisonDTO dto);
    Livraison getLivraisonById(Long id);
    List<Livraison> getAllLivraisons();
    List<Livraison> getLivraisonsByStatut(LivraisonStatus statut);
    List<Livraison> getLivraisonsByLivreur(Long livreurId);
}
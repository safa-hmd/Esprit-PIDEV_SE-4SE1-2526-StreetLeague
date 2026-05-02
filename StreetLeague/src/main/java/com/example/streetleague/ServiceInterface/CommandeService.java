package com.example.streetleague.ServiceInterface;

import com.example.streetleague.domain.Commande;
import com.example.streetleague.dto.CommandeDTO;

import java.util.List;

public interface CommandeService {

    Commande createCommande(CommandeDTO dto);
    Commande updateCommandeStatus(Long id, String statut);
    Commande getCommandeById(Long id);
    List<Commande> getAllCommandes();
    Long checkout(Long userId);
}
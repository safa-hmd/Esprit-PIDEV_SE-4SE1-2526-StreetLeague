package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceInterface.CommandeService;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.CommandeDTO;
import com.example.streetleague.dto.LigneCommandeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeServiceImp implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final UserRepository userRepository;
    private final MaterielRepository materielRepository;
    private final PanierRepository panierRepository;
    private final LignePanierRepository lignePanierRepository;

    @Override
    public Commande createCommande(@Valid CommandeDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        List<LigneCommande> lignes = new ArrayList<>();
        double montantTotal = 0;

        for (LigneCommandeDTO ligneDTO : dto.getLignes()) {
            Materiel materiel = materielRepository.findById(ligneDTO.getMaterielId())
                    .orElseThrow(() -> new RuntimeException("Materiel introuvable"));

            LigneCommande ligne = LigneCommande.builder()
                    .materiel(materiel)
                    .quantite(ligneDTO.getQuantite())
                    .commande(null) // sera lié après
                    .build();

            montantTotal += materiel.getPrix() * ligneDTO.getQuantite();
            lignes.add(ligne);
        }

        Commande commande = Commande.builder()
                .user(user)
                .montantTotal(montantTotal)
                .statut(CommandeStatus.valueOf(dto.getStatut()))
                .lignes(lignes)
                .build();

        // Lier les lignes à la commande
        lignes.forEach(l -> l.setCommande(commande));

        return commandeRepository.save(commande);
    }

    @Override
    public Commande updateCommandeStatus(Long id, String statut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));
        commande.setStatut(CommandeStatus.valueOf(statut));
        return commandeRepository.save(commande);
    }

    @Override
    public Commande getCommandeById(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));
    }

    @Override
    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }

    @Override
    @Transactional
    public Long checkout(Long userId) {

        // 1️⃣ récupérer utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 2️⃣ récupérer panier
        Panier panier = panierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Panier introuvable"));

        List<LignePanier> lignesPanier = panier.getLignes();
        if (lignesPanier == null || lignesPanier.isEmpty()) {
            throw new RuntimeException("Panier vide !");
        }

        // 3️⃣ calcul montant total et vérifier stock
        double total = 0;
        List<LigneCommande> lignesCommande = new ArrayList<>();

        for (LignePanier ligne : lignesPanier) {
            Materiel materiel = ligne.getMateriel();
            if (materiel.getQuantiteStock() < ligne.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour : " + materiel.getNom());
            }

            // décrémenter stock
            materiel.setQuantiteStock(materiel.getQuantiteStock() - ligne.getQuantite());
            materielRepository.save(materiel);

            // préparer ligne commande
            lignesCommande.add(
                    LigneCommande.builder()
                            .materiel(materiel)
                            .quantite(ligne.getQuantite())
                            .prixUnitaire(materiel.getPrix())
                            .build()
            );

            total += materiel.getPrix() * ligne.getQuantite();
        }

        // 4️⃣ créer commande avec toutes les lignes
        Commande commande = Commande.builder()
                .user(user)
                .statut(CommandeStatus.PREPAREE)
                .montantTotal(total)
                .lignes(lignesCommande)
                .build();

        // lier chaque ligne à la commande
        lignesCommande.forEach(l -> l.setCommande(commande));

        // 5️⃣ sauvegarder commande (sauvegarde unique)
        commandeRepository.save(commande);

        // 6️⃣ vider panier
        lignePanierRepository.deleteAll(lignesPanier);

        return commande.getId();
    }
}
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
// ajout import
import com.example.streetleague.ServiceImp.PromoEngineService;
import java.time.LocalDateTime;
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
    private final LivraisonRepository livraisonRepository;
    private final PromoEngineService promoEngineService;

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
                    .commande(null)
                    .build();

            montantTotal += materiel.getPrix() * ligneDTO.getQuantite();
            lignes.add(ligne);
        }

        Commande commande = Commande.builder()
                .user(user)
                .montantTotal(montantTotal)
                .statut(CommandeStatus.valueOf(dto.getStatut()))
                .latitudeClient(dto.getLatitudeClient() != null ? dto.getLatitudeClient() : 0.0)
                .longitudeClient(dto.getLongitudeClient() != null ? dto.getLongitudeClient() : 0.0)
                .adresseLivraison(dto.getAdresseLivraison())
                .lignes(lignes)
                .build();

        lignes.forEach(l -> l.setCommande(commande));
        return commandeRepository.save(commande);
    }

    // ════════════════════════════════════════════════════
    // PARTIE 2 — Création auto livraison quand EN_ATTENTE → VALIDEE
    // ════════════════════════════════════════════════════
    @Override
    public Commande updateCommandeStatus(Long id, String statut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        CommandeStatus ancienStatut = commande.getStatut();
        CommandeStatus nouveauStatut = CommandeStatus.valueOf(statut);

        commande.setStatut(nouveauStatut);
        commandeRepository.save(commande);

        // ── Auto-création livraison si EN_ATTENTE → VALIDEE ──
        if (ancienStatut == CommandeStatus.EN_ATTENTE
                && nouveauStatut == CommandeStatus.VALIDEE) {
            creerLivraisonAutomatique(commande);
        }

        return commande;
    }

    private void creerLivraisonAutomatique(Commande commande) {
        Livraison livraison = Livraison.builder()
                .commande(commande)
                .livreur(null)                          // sera assigné par le scheduler
                .adresse(commande.getAdresseLivraison() != null
                        ? commande.getAdresseLivraison() : "Adresse à définir")
                .latitudeClient(commande.getLatitudeClient())
                .longitudeClient(commande.getLongitudeClient())
                .statut(LivraisonStatus.PREPAREE)
                .priorite(Priorite.NORMAL)
                .dateCreation(LocalDateTime.now())
                .fraisLivraison(7.0)                    // frais par défaut
                .build();

        livraisonRepository.save(livraison);
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

   /* @Override
    @Transactional
    public Long checkout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Panier panier = panierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Panier introuvable"));

        List<LignePanier> lignesPanier = panier.getLignes();
        if (lignesPanier == null || lignesPanier.isEmpty()) {
            throw new RuntimeException("Panier vide !");
        }

        double total = 0;
        List<LigneCommande> lignesCommande = new ArrayList<>();

        for (LignePanier ligne : lignesPanier) {
            Materiel materiel = ligne.getMateriel();
            if (materiel.getQuantiteStock() < ligne.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour : " + materiel.getNom());
            }

            materiel.setQuantiteStock(materiel.getQuantiteStock() - ligne.getQuantite());
            materielRepository.save(materiel);

            lignesCommande.add(LigneCommande.builder()
                    .materiel(materiel)
                    .quantite(ligne.getQuantite())
                    .prixUnitaire(materiel.getPrix())
                    .build());

            total += materiel.getPrix() * ligne.getQuantite();
        }

        Commande commande = Commande.builder()
                .user(user)
                .statut(CommandeStatus.EN_ATTENTE)
                .montantTotal(total)
                .lignes(lignesCommande)
                .build();

        lignesCommande.forEach(l -> l.setCommande(commande));
        commandeRepository.save(commande);
        lignePanierRepository.deleteAll(lignesPanier);

        return commande.getId();
    }*/
   @Override
   @Transactional
   public Long checkout(Long userId) {

       User user = userRepository.findById(userId)
               .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

       Panier panier = panierRepository.findByUserId(userId)
               .orElseThrow(() -> new RuntimeException("Panier introuvable"));

       List<LignePanier> lignesPanier = panier.getLignes();

       if (lignesPanier == null || lignesPanier.isEmpty()) {
           throw new RuntimeException("Panier vide !");
       }

       double total = 0;
       List<LigneCommande> lignesCommande = new ArrayList<>();

       for (LignePanier ligne : lignesPanier) {

           Materiel materiel = ligne.getMateriel();

           materiel.setQuantiteStock(
                   materiel.getQuantiteStock() - ligne.getQuantite()
           );
           materielRepository.save(materiel);

           lignesCommande.add(
                   LigneCommande.builder()
                           .materiel(materiel)
                           .quantite(ligne.getQuantite())
                           .prixUnitaire(materiel.getPrix())
                           .build()
           );

           total += materiel.getPrix() * ligne.getQuantite();
       }

       // 🔥 BIG CART RULE
       if (total >= 200) {
           promoEngineService.generateBigCartPromo(user, total);
       }

       Commande commande = Commande.builder()
               .user(user)
               .statut(CommandeStatus.EN_ATTENTE)
               .montantTotal(total)
               .lignes(lignesCommande)
               .build();

       lignesCommande.forEach(l -> l.setCommande(commande));
       commandeRepository.save(commande);

       lignePanierRepository.deleteAll(lignesPanier);

       // 🔥 FIDELITY RULE
       promoEngineService.generateFidelityPromo(user);

       return commande.getId();
   }
}
package com.example.streetleague.ServiceImp;

import com.example.streetleague.Repository.*;
import com.example.streetleague.ServiceInterface.PanierService;
import com.example.streetleague.domain.*;
import com.example.streetleague.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PanierServiceImp implements PanierService {

    private final PanierRepository panierRepository;
    private final LignePanierRepository lignePanierRepository;
    private final UserRepository userRepository;
    private final MaterielRepository materielRepository;
    private final PromoService promoService; // ← AJOUTEZ CETTE LIGNE
    private final PromoEngineService promoEngineService;


    // 🟢 ADD TO CART
    @Override
    public void addToCart(AddToCartDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Materiel materiel = materielRepository.findById(dto.getMaterielId())
                .orElseThrow(() -> new RuntimeException("Materiel introuvable"));

        Panier panier = panierRepository.findByUserId(user.getId())
                .orElseGet(() -> panierRepository.save(Panier.builder().user(user).lignes(new ArrayList<>()).build()));

        Optional<LignePanier> existing = panier.getLignes()
                .stream()
                .filter(l -> l.getMateriel().getId().equals(materiel.getId()))
                .findFirst();

        if(existing.isPresent()){
            LignePanier ligne = existing.get();
            ligne.setQuantite(ligne.getQuantite() + dto.getQuantite());
        } else {
            LignePanier ligne = LignePanier.builder()
                    .panier(panier)
                    .materiel(materiel)
                    .quantite(dto.getQuantite())
                    .build();
            lignePanierRepository.save(ligne);
            panier.getLignes().add(ligne);
        }

        panierRepository.save(panier);
    }

    // 🟢 GET CART
    @Override


    public PanierResponseDTO getUserCart(Long userId) {

        Panier panier = panierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Panier vide"));

        // 🔥 AUTO PROMO TRIGGER
        User user = panier.getUser();

        promoEngineService.generateWelcomePromo(user);
        promoEngineService.generateAbandonedCartPromo(user);

        List<LignePanierResponseDTO> lignesDTO = new ArrayList<>();
        double total = 0;

        for (LignePanier ligne : panier.getLignes()) {
            double sousTotal = ligne.getMateriel().getPrix() * ligne.getQuantite();
            total += sousTotal;

            lignesDTO.add(
                    LignePanierResponseDTO.builder()
                            .ligneId(ligne.getId())
                            .materielId(ligne.getMateriel().getId())
                            .materielNom(ligne.getMateriel().getNom())
                            .prix(ligne.getMateriel().getPrix())
                            .quantite(ligne.getQuantite())
                            .sousTotal(sousTotal)
                            .build()
            );
        }

        return PanierResponseDTO.builder()
                .panierId(panier.getId())
                .userId(userId)
                .lignes(lignesDTO)
                .total(total)
                .build();
    }

    // 🟢 UPDATE QTE
    @Override
    public void updateQuantity(UpdateCartDTO dto) {
        LignePanier ligne = lignePanierRepository.findById(dto.getLignePanierId())
                .orElseThrow(() -> new RuntimeException("Ligne introuvable"));

        ligne.setQuantite(dto.getQuantite());
    }

    // 🟢 REMOVE ITEM
    @Override
    public void removeItem(Long lignePanierId) {
        lignePanierRepository.deleteById(lignePanierId);
    }

    // 🟢 CLEAR CART
    @Override
    public void clearCart(Long userId) {
        Panier panier = panierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Panier introuvable"));

        lignePanierRepository.deleteAll(panier.getLignes());
        panier.getLignes().clear();
    }

    private double calculateTotal(List<CartItemDTO> items) {
        return items.stream()
                .mapToDouble(item -> item.getQuantite() * item.getPrixUnitaire())
                .sum();
    }

    // Méthode pour extraire les IDs des catégories du panier
    private List<Long> extractCategoryIds(List<CartItemDTO> items) {
        return items.stream()
                .map(CartItemDTO::getCategorieId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
    public PromoValidationDTO validatePromo(String code, Long userId, List<CartItemDTO> items) {
        double cartTotal = calculateTotal(items);
        List<Long> categoryIds = extractCategoryIds(items);
        return promoService.validate(code, userId, cartTotal, categoryIds);
    }
}
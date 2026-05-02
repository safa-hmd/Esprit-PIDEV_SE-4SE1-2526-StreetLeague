package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.PanierService;
import com.example.streetleague.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/panier")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PanierController {

    private final PanierService panierService;

    @PostMapping("/add")
    public String addToCart(@Valid @RequestBody AddToCartDTO dto){
        panierService.addToCart(dto);
        return "Produit ajouté au panier";
    }

    @GetMapping("/{userId}")
    public PanierResponseDTO getCart(@PathVariable Long userId){
        return panierService.getUserCart(userId);
    }

    @PutMapping("/update")
    public String updateQuantity(@Valid @RequestBody UpdateCartDTO dto){
        panierService.updateQuantity(dto);
        return "Quantité modifiée";
    }

    @DeleteMapping("/remove/{ligneId}")
    public String removeItem(@PathVariable Long ligneId){
        panierService.removeItem(ligneId);
        return "Produit supprimé";
    }

    @DeleteMapping("/clear/{userId}")
    public String clearCart(@PathVariable Long userId){
        panierService.clearCart(userId);
        return "Panier vidé";
    }
}
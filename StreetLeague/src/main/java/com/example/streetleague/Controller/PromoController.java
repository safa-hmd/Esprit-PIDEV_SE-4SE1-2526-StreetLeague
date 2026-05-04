package com.example.streetleague.Controller;

import com.example.streetleague.ServiceImp.PromoService;
import com.example.streetleague.dto.PromoOfferDTO;
import com.example.streetleague.dto.PromoValidationDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PromoController {

    private final PromoService promoService;

    @PostMapping("/validate")
    public PromoValidationDTO validatePromo(@RequestBody PromoValidationRequest request) {
        return promoService.validate(
                request.getCode(),
                request.getUserId(),
                request.getCartTotal(),
                request.getCategoryIds()
        );
    }
    @GetMapping("/my-offers")
    public List<PromoOfferDTO> getPersonalOffers(@RequestParam Long userId) {
        return promoService.getActivePersonalOffers(userId);
    }
    // DTO pour la requête
    @Data
    public static class PromoValidationRequest {
        private String code;
        private Long userId;
        private double cartTotal;
        private List<Long> categoryIds;
    }
}
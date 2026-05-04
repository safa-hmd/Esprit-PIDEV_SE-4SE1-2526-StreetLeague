package com.example.streetleague.Controller;

import com.example.streetleague.dto.SuggestedPriceResponse;
import com.example.streetleague.ServiceImp.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://streetleaguefrontend.azurewebsites.net"
}, allowCredentials = "true")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * GET /api/pricing/field/{fieldId}/suggest?duration=2
     * Retourne le prix suggéré pour un terrain donné.
     * duration = nb d'heures de réservation (default 1)
     */
    @GetMapping("/field/{fieldId}/suggest")
    public ResponseEntity<SuggestedPriceResponse> suggestPrice(
            @PathVariable Long fieldId,
            @RequestParam(defaultValue = "1") int duration
    ) {
        SuggestedPriceResponse response = pricingService.getSuggestedPrice(fieldId, duration);
        return ResponseEntity.ok(response);
    }

    /**
     * * GET /api/pricing/suggest?sportType=FOOTBALL&location=Tunis&capacity=22&duration=1
     *  * Pour le modal Add Field — pas de fieldId, on utilise les valeurs du form
     *  */
    @GetMapping("/suggest")
    public ResponseEntity<SuggestedPriceResponse> suggestPriceFromParams(
     @RequestParam String sportType,
     @RequestParam String location,
     @RequestParam int capacity,
     @RequestParam(defaultValue = "1") int duration
     ) {
     SuggestedPriceResponse response = pricingService.getSuggestedPriceFromParams(
                 sportType, location, capacity, duration
          );
     return ResponseEntity.ok(response);
     }
}
package com.example.streetleague.Controller;

import com.example.streetleague.ServiceInterface.DietService;
import com.example.streetleague.dto.DietRequestDTO;
import com.example.streetleague.dto.DietResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diet")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DietController {

    private final DietService dietService;

    // Angular يبعث age + bmi → يرجعله diet plan
    @PostMapping("/recommend/{userId}")
    public ResponseEntity<DietResponseDTO> recommend(
            @PathVariable Long userId,
            @RequestBody DietRequestDTO request) {
        return ResponseEntity.ok(
                dietService.getDietRecommendation(request, userId)
        );
    }

    // يجيب آخر diet plan للـ user
    @GetMapping("/last/{userId}")
    public ResponseEntity<DietResponseDTO> getLastDiet(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                dietService.getLastDiet(userId)
        );
    }
}
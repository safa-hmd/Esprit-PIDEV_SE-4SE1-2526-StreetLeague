package com.example.streetleague.ServiceImp;

import com.example.streetleague.Entity.Diet;
import com.example.streetleague.Repository.DietRepository;
import com.example.streetleague.ServiceInterface.DietService;
import com.example.streetleague.domain.User;
import com.example.streetleague.dto.DietRequestDTO;
import com.example.streetleague.dto.DietResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DietServiceIMPL implements DietService {

    private final DietRepository dietRepository;
    private final RestTemplate restTemplate;

    private static final String FLASK_URL = "http://localhost:5000/predict";

    @Override
    public DietResponseDTO getDietRecommendation(DietRequestDTO request, Long userId) {

        // 1. نبعثو للـ Flask
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "age", request.getAge(),
                "bmi", request.getBmi()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                FLASK_URL, entity, Map.class
        );

        Map<String, Object> flaskResult = response.getBody();

        // 2. نحفظو في DB
        Diet diet = Diet.builder()
                .goal((String) flaskResult.get("goal"))
                .status((String) flaskResult.get("status"))
                .dailyCalories((Integer) flaskResult.get("daily_calories"))
                .dietPlan((List<String>) flaskResult.get("diet_plan"))
                .createdDate(LocalDate.now())
                .user(User.builder().id(userId).build())
                .build();

        Diet saved = dietRepository.save(diet);

        // 3. نرجعو للـ Angular
        return DietResponseDTO.builder()
                .id(saved.getId())
                .goal(saved.getGoal())
                .status(saved.getStatus())
                .dailyCalories(saved.getDailyCalories())
                .dietPlan(saved.getDietPlan())
                .createdDate(saved.getCreatedDate())
                .build();
    }

    @Override
    public DietResponseDTO getLastDiet(Long userId) {
        Diet diet = dietRepository.findTopByUserIdOrderByCreatedDateDesc(userId)
                .orElseThrow(() -> new RuntimeException("No diet found for user"));

        return DietResponseDTO.builder()
                .id(diet.getId())
                .goal(diet.getGoal())
                .status(diet.getStatus())
                .dailyCalories(diet.getDailyCalories())
                .dietPlan(diet.getDietPlan())
                .createdDate(diet.getCreatedDate())
                .build();
    }
}
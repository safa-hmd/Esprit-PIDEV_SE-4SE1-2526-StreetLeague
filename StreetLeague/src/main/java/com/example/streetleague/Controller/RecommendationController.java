package com.example.streetleague.Controller;

import com.example.streetleague.Repository.FieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Value("${flask.api.url}")
    private String flaskUrl;

    private final RestTemplate    restTemplate;
    private final FieldRepository fieldRepository;

    // ════════════════════════════════════════════════════════════════
    // GET /api/recommend/fields/{userId}?lat=36.8&lng=10.1
    // Called by Angular field-recommender component
    // ════════════════════════════════════════════════════════════════
    @GetMapping("/fields/{userId}")
    public ResponseEntity<List> recommendFields(
            @PathVariable Long userId,
            @RequestParam double lat,
            @RequestParam double lng) {

        List<Map<String, Object>> fields = getAllFieldsFromDb();

        if (fields.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("userId",  userId);
        body.put("userLat", lat);
        body.put("userLng", lng);
        body.put("fields",  fields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<List> resp = restTemplate.postForEntity(
                    flaskUrl + "/api/recommend/fields", request, List.class);
            return ResponseEntity.ok(resp.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Collections.emptyList());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // GET /api/recommend/slot/{userId}/{fieldId}?lat=36.8&lng=10.1
    // Called by Angular field-recommender when user selects a field
    // ════════════════════════════════════════════════════════════════
    @GetMapping("/slot/{userId}/{fieldId}")
    public ResponseEntity<List> recommendSlot(
            @PathVariable Long userId,
            @PathVariable Long fieldId,
            @RequestParam double lat,
            @RequestParam double lng) {

        Map<String, Object> field = fieldRepository.findById(fieldId)
                .map(f -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",          f.getId());
                    m.put("name",        f.getName());
                    m.put("lat",         f.getLatitude());
                    m.put("lng",         f.getLongitude());
                    m.put("capacity",    f.getCapacity());
                    m.put("isAvailable", f.isAvailable());
                    m.put("pressure",    0.3);
                    m.put("pricePerHour", f.getPricePerHour());
                    m.put("location",    f.getLocation());
                    return m;
                })
                .orElse(new HashMap<>());

        Map<String, Object> body = new HashMap<>();
        body.put("userId",  userId);
        body.put("fieldId", fieldId);
        body.put("userLat", lat);
        body.put("userLng", lng);
        body.put("field",   field);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<List> resp = restTemplate.postForEntity(
                    flaskUrl + "/api/recommend/slots", request, List.class);
            return ResponseEntity.ok(resp.getBody() != null ? resp.getBody() : Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Collections.emptyList());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // HELPER — Load all fields from DB
    // ════════════════════════════════════════════════════════════════
    private List<Map<String, Object>> getAllFieldsFromDb() {
        return fieldRepository.findAll().stream()
                .filter(f -> f.getLatitude() != null && f.getLongitude() != null)
                .map(f -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id",          f.getId());
                    m.put("name",        f.getName());
                    m.put("lat",         f.getLatitude());
                    m.put("lng",         f.getLongitude());
                    m.put("capacity",    f.getCapacity());
                    m.put("isAvailable", f.isAvailable());
                    m.put("pressure",    0.3);
                    m.put("pricePerHour", f.getPricePerHour());
                    m.put("location",    f.getLocation() != null ? f.getLocation() : "");
                    return m;
                })
                .collect(Collectors.toList());
    }
}
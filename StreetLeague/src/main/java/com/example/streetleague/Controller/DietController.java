package com.example.streetleague.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/diet")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://streetleaguefrontend.azurewebsites.net"
})
public class DietController {

    private final RestTemplate restTemplate;

    @Value("${fastapi.api.url:http://localhost:5000}")
    private String fastapiApiUrl;

    @PostMapping("/recommend/{userId}")
    public ResponseEntity<Map> recommend(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        String url = fastapiApiUrl + "/predict";
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        return ResponseEntity.ok(response.getBody());
    }
}
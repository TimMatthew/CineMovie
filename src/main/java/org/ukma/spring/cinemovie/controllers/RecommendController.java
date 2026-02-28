package org.ukma.spring.cinemovie.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public ResponseEntity<?> proxyRecommend(@RequestBody Map<String, Object> request) {
        String flaskUrl = "http://localhost:5000/recommend";
        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);
        return ResponseEntity.ok(response.getBody());
    }
}

package com.example.Demo4Zakhid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @RateLimit(maxRequests = 5, durationInMinutes = 1) // Пример: максимум 5 запросов в минуту
    @GetMapping("/limited")
    public ResponseEntity<String> limitedEndpoint() {
        return ResponseEntity.ok("This endpoint is rate-limited.");
    }
}
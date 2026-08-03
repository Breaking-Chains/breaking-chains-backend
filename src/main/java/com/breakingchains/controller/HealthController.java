package com.breakingchains.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@SecurityRequirements
@Tag(name = "System Health", description = "System health check endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Service Health Check", description = "Retrieves operational health and application deployment metadata.")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "breaking-chains-backend",
                "framework", "Spring Boot 3 (Java 17)"
        ));
    }
}

package com.clipers.clipers.controller;

import com.clipers.clipers.dto.matching.HealthResponseDTO;
import com.clipers.clipers.service.AIMatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIMatchingService aiMatchingService;

    public AIController(AIMatchingService aiMatchingService) {
        this.aiMatchingService = aiMatchingService;
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponseDTO> health() {
        return ResponseEntity.ok(aiMatchingService.checkHealth());
    }
}
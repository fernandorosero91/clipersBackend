package com.clipers.clipers.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            return ResponseEntity.ok("{\"status\": \"ok\", \"message\": \"¡Backend Clipers funcionando correctamente! 🎉\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Error interno del servidor\"}");
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}

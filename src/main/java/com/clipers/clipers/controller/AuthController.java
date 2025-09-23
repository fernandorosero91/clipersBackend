package com.clipers.clipers.controller;

import com.clipers.clipers.dto.AuthRequest;
import com.clipers.clipers.dto.AuthResponse;
import com.clipers.clipers.dto.RegisterRequest;
import com.clipers.clipers.dto.UserDTO;
import com.clipers.clipers.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Credenciales inválidas", e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new RuntimeException("Refresh token es requerido");
            }
            
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al refrescar token: " + e.getMessage(), e);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        try {
            UserDTO user = authService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuario actual", e);
        }
    }

}

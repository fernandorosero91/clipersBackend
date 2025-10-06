package com.clipers.clipers.controller;

import com.clipers.clipers.entity.ATSProfile;
import com.clipers.clipers.repository.UserRepository;
import com.clipers.clipers.service.ATSProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para gestión de perfiles ATS
 */
@RestController
@RequestMapping("/api/ats-profiles")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ATSProfileService atsProfileService;
    private final UserRepository userRepository;

    @Autowired
    public ProfileController(ATSProfileService atsProfileService, UserRepository userRepository) {
        this.atsProfileService = atsProfileService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ATSProfile> getATSProfile() {
        try {
            String userId = getCurrentUserId();
            return atsProfileService.findByUserId(userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener perfil ATS: " + e.getMessage(), e);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ATSProfile> getATSProfileByUserId(@PathVariable String userId) {
        return atsProfileService.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/ats")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ATSProfile> createATSProfile(@RequestBody Map<String, Object> request) {
        try {
            String userId = getCurrentUserId();
            String summary = (String) request.get("summary");
            String cliperId = (String) request.get("cliperId");
            
            ATSProfile profile = atsProfileService.createProfile(userId, summary, cliperId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear perfil ATS: " + e.getMessage(), e);
        }
    }

    @PutMapping("/ats")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ATSProfile> updateATSProfile(@RequestBody Map<String, Object> request) {
        try {
            String userId = getCurrentUserId();
            String summary = (String) request.get("summary");
            
            ATSProfile profile = atsProfileService.updateProfile(userId, summary);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar perfil ATS: " + e.getMessage(), e);
        }
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Obtener el email del JWT token y buscar el usuario real en la base de datos
        String email = auth.getName();
        // Buscar el usuario por email y devolver su ID real
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }
}

package com.clipers.clipers.controller;

import com.clipers.clipers.entity.ATSProfile;
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
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ATSProfileService atsProfileService;

    @Autowired
    public ProfileController(ATSProfileService atsProfileService) {
        this.atsProfileService = atsProfileService;
    }

    @GetMapping("/ats")
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

    @GetMapping("/ats/{userId}")
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
        return "user-" + Math.abs(auth.getName().hashCode());
    }
}

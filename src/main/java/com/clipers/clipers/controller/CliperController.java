package com.clipers.clipers.controller;

import com.clipers.clipers.entity.Cliper;
import com.clipers.clipers.service.CliperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clipers")
@CrossOrigin(origins = "*")
public class CliperController {

    private final CliperService cliperService;

    @Autowired
    public CliperController(CliperService cliperService) {
        this.cliperService = cliperService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Cliper> uploadCliper(
            @RequestParam("video") MultipartFile videoFile,
            @RequestParam("title") String title,
            @RequestParam("description") String description) {
        try {
            String userId = getCurrentUserId();
            
            // Simular guardado de archivo y obtener URL
            String videoUrl = saveVideoFile(videoFile);
            Integer duration = extractVideoDuration(videoFile);
            
            Cliper cliper = cliperService.createCliper(userId, title, description, videoUrl, duration);
            return ResponseEntity.ok(cliper);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir cliper: " + e.getMessage(), e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Cliper> createCliper(@RequestBody Map<String, Object> request) {
        try {
            String userId = getCurrentUserId();
            
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String videoUrl = (String) request.get("videoUrl");
            Integer duration = (Integer) request.get("duration");
            
            Cliper cliper = cliperService.createCliper(userId, title, description, videoUrl, duration);
            return ResponseEntity.ok(cliper);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear cliper: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliper> getCliper(@PathVariable String id) {
        return cliperService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Cliper>> getClipersByUser(@PathVariable String userId) {
        List<Cliper> clipers = cliperService.findByUserId(userId);
        return ResponseEntity.ok(clipers);
    }

    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<Page<Cliper>> getClipersByUserPaginated(
            @PathVariable String userId, Pageable pageable) {
        Page<Cliper> clipers = cliperService.findByUserId(userId, pageable);
        return ResponseEntity.ok(clipers);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getClipers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliper> clipersPage = cliperService.findProcessedClipers(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("clipers", clipersPage.getContent());
        response.put("hasMore", clipersPage.hasNext());
        response.put("totalPages", clipersPage.getTotalPages());
        response.put("currentPage", page);
        response.put("totalElements", clipersPage.getTotalElements());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getProcessedClipers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliper> clipersPage = cliperService.findProcessedClipers(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("clipers", clipersPage.getContent());
        response.put("hasMore", clipersPage.hasNext());
        response.put("totalPages", clipersPage.getTotalPages());
        response.put("currentPage", page);
        response.put("totalElements", clipersPage.getTotalElements());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Cliper>> searchClipers(
            @RequestParam String query, Pageable pageable) {
        Page<Cliper> clipers = cliperService.searchClipers(query, pageable);
        return ResponseEntity.ok(clipers);
    }

    @GetMapping("/by-skill")
    public ResponseEntity<List<Cliper>> getClipersBySkill(@RequestParam String skill) {
        List<Cliper> clipers = cliperService.findBySkill(skill);
        return ResponseEntity.ok(clipers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Cliper> updateCliper(
            @PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            String description = request.get("description");
            
            Cliper updatedCliper = cliperService.updateCliper(id, title, description);
            return ResponseEntity.ok(updatedCliper);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar cliper: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Void> deleteCliper(@PathVariable String id) {
        try {
            cliperService.deleteCliper(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar cliper: " + e.getMessage(), e);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Cliper>> getClipersByStatus(@PathVariable String status) {
        try {
            Cliper.Status statusEnum = Cliper.Status.valueOf(status.toUpperCase());
            List<Cliper> clipers = cliperService.findByStatus(statusEnum);
            return ResponseEntity.ok(clipers);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + status, e);
        }
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // En producción, extraería el userId del JWT token
        // Por ahora, simulamos con un ID basado en el email
        return "user-" + Math.abs(auth.getName().hashCode());
    }

    private String saveVideoFile(MultipartFile videoFile) {
        // Simular guardado de archivo
        // En producción, guardaría en S3, local storage, etc.
        String fileName = "video_" + System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
        return "/uploads/videos/" + fileName;
    }

    private Integer extractVideoDuration(MultipartFile videoFile) {
        // Simular extracción de duración
        // En producción, usaría FFmpeg para obtener la duración real
        return 30 + (int)(Math.random() * 120); // 30-150 segundos
    }
}

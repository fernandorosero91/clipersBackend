package com.clipers.clipers.controller;

import com.clipers.clipers.dto.UserDTO;
import com.clipers.clipers.entity.JobApplication;
import com.clipers.clipers.service.ApplicationService;
import com.clipers.clipers.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final AuthService authService;

    @Autowired
    public ApplicationController(ApplicationService applicationService, AuthService authService) {
        this.applicationService = applicationService;
        this.authService = authService;
    }

    @GetMapping("/candidates/{candidateId}/applications")
    @PreAuthorize("hasAnyRole('CANDIDATE','ADMIN')")
    public ResponseEntity<List<JobApplication>> getApplicationsForCandidate(@PathVariable String candidateId) {
        // Regla de acceso: el candidato solo puede ver sus propias aplicaciones; admin puede ver cualquier
        UserDTO currentUser = authService.getCurrentUser();
        boolean isAdmin = currentUser.getRole() != null && currentUser.getRole().equals("ADMIN");
        if (!isAdmin && !currentUser.getId().equals(candidateId)) {
            throw new RuntimeException("No autorizado para ver aplicaciones de otro candidato");
        }
        List<JobApplication> applications = applicationService.getApplicationsForCandidate(candidateId);
        return ResponseEntity.ok(applications);
    }
}
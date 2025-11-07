package com.clipers.clipers.controller;

import com.clipers.clipers.dto.CompanyDTO;
import com.clipers.clipers.entity.Company;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.repository.CompanyRepository;
import com.clipers.clipers.security.CustomUserDetailsService.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<CompanyDTO> getMyCompany(@AuthenticationPrincipal CustomUserPrincipal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        User user = principal.getUser();
        Optional<Company> companyOpt = companyRepository.findByUserId(user.getId());
        return companyOpt.map(company -> ResponseEntity.ok(toDTO(company)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CompanyDTO> getCompanyByUserId(@PathVariable String userId) {
        Optional<Company> companyOpt = companyRepository.findByUserId(userId);
        return companyOpt.map(company -> ResponseEntity.ok(toDTO(company)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<CompanyDTO> updateCompanyProfile(@RequestBody Map<String, String> request,
                                                           @AuthenticationPrincipal CustomUserPrincipal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        User user = principal.getUser();
        Company company = companyRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

        if (request.containsKey("name")) company.setName(request.get("name"));
        if (request.containsKey("description")) company.setDescription(request.get("description"));
        if (request.containsKey("industry")) company.setIndustry(request.get("industry"));
        if (request.containsKey("size")) company.setSize(request.get("size"));
        if (request.containsKey("website")) company.setWebsite(request.get("website"));
        if (request.containsKey("location")) company.setLocation(request.get("location"));
        if (request.containsKey("logo")) company.setLogo(request.get("logo"));

        Company saved = companyRepository.save(company);
        return ResponseEntity.ok(toDTO(saved));
    }

    @PostMapping("/upload/logo")
    public ResponseEntity<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file,
                                                          @AuthenticationPrincipal CustomUserPrincipal principal) {
        try {
            if (principal == null) {
                throw new RuntimeException("Usuario no autenticado");
            }
            if (file.isEmpty()) {
                throw new RuntimeException("Archivo vacío");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Solo se permiten archivos de imagen");
            }

            Path uploadPath = Paths.get("uploads", "logos");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Construir baseUrl dinamicamente segun el entorno actual
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String imageUrl = baseUrl + "/uploads/logos/" + filename;

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("filename", filename);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir logo: " + e.getMessage(), e);
        }
    }

    private CompanyDTO toDTO(Company company) {
        // Usar el constructor que acepta Company para evitar errores de firma
        return new CompanyDTO(company);
    }
}
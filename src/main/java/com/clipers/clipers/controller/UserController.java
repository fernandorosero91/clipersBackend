package com.clipers.clipers.controller;

import com.clipers.clipers.dto.UserDTO;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.repository.UserRepository;
import com.clipers.clipers.security.CustomUserDetailsService.CustomUserPrincipal;
import com.clipers.clipers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        Optional<UserDTO> userOpt = userService.findById(userId);
        return userOpt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody Map<String, String> request,
                                                 @AuthenticationPrincipal CustomUserPrincipal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        String userId = principal.getUser().getId();

        UserDTO dto = new UserDTO();
        if (request.containsKey("firstName")) dto.setFirstName(request.get("firstName"));
        if (request.containsKey("lastName")) dto.setLastName(request.get("lastName"));
        if (request.containsKey("profileImage")) dto.setProfileImage(request.get("profileImage"));

        UserDTO updated = userService.updateUser(userId, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/upload/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file,
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

            Path uploadPath = Paths.get("uploads", "avatars");
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

            // Construir baseUrl dinamicamente segun el entorno actual (localhost o dominio)
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String imageUrl = baseUrl + "/uploads/avatars/" + filename;

            Map<String, String> result = new HashMap<>();
            result.put("imageUrl", imageUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir avatar: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/profile/avatar")
    public ResponseEntity<Map<String, String>> deleteAvatar(@AuthenticationPrincipal CustomUserPrincipal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        String userId = principal.getUser().getId();

        // Limpiar la imagen de perfil estableciendo cadena vacía (updateUser ignora null)
        UserDTO req = new UserDTO();
        req.setProfileImage("");
        userService.updateUser(userId, req);

        Map<String, String> result = new HashMap<>();
        result.put("status", "ok");
        return ResponseEntity.ok(result);
    }
}
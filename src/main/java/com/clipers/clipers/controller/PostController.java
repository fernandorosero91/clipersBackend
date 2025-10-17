package com.clipers.clipers.controller;

import com.clipers.clipers.entity.Comment;
import com.clipers.clipers.entity.Post;
import com.clipers.clipers.entity.User;
import com.clipers.clipers.security.CustomUserDetailsService.CustomUserPrincipal;
import com.clipers.clipers.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador que implementa Facade Pattern implícitamente
 * Proporciona una interfaz simplificada para las operaciones del feed social
 */
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Map<String, Object> request, @AuthenticationPrincipal CustomUserPrincipal principal) {
        try {
            if (principal == null) {
                throw new RuntimeException("Usuario no autenticado");
            }

            User user = principal.getUser();
            String userId = user.getId();
            String content = (String) request.get("content");
            String imageUrl = (String) request.get("imageUrl");
            String videoUrl = (String) request.get("videoUrl");
            String typeStr = (String) request.get("type");

            Post.PostType type = typeStr != null ? Post.PostType.valueOf(typeStr.toUpperCase()) : Post.PostType.TEXT;

            Post post = postService.createPost(userId, content, imageUrl, videoUrl, type);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear publicación: " + e.getMessage(), e);
        }
    }

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserPrincipal principal) {
        try {
            if (principal == null) {
                throw new RuntimeException("Usuario no autenticado");
            }

            if (file.isEmpty()) {
                throw new RuntimeException("Archivo vacío");
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Solo se permiten archivos de imagen");
            }

            // Crear directorio si no existe
            Path uploadPath = Paths.get("uploads", "images");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // Guardar archivo
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Crear URL completa con el dominio del backend
            String baseUrl = "https://backend.sufactura.store/:8080";
            String imageUrl = baseUrl + "/uploads/images/" + filename;

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("filename", filename);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id) {
        return postService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postService.getFeed(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsPage.getContent());
        response.put("hasMore", postsPage.hasNext());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("currentPage", page);
        response.put("totalElements", postsPage.getTotalElements());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public ResponseEntity<Map<String, Object>> getFeedAlternate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return getFeed(page, size);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable String userId) {
        List<Post> posts = postService.findByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable String id, @AuthenticationPrincipal CustomUserPrincipal principal) {
        try {
            if (principal == null) {
                throw new RuntimeException("Usuario no autenticado");
            }
            User user = principal.getUser();
            String userId = user.getId();
            postService.toggleLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException("Error al dar like: " + e.getMessage(), e);
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable String id, @RequestBody Map<String, String> request, @AuthenticationPrincipal CustomUserPrincipal principal) {
        try {
            if (principal == null) {
                throw new RuntimeException("Usuario no autenticado");
            }
            User user = principal.getUser();
            String userId = user.getId();
            String content = request.get("content");

            Comment comment = postService.addComment(id, userId, content);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar comentario: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String id) {
        List<Comment> comments = postService.getComments(id);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            Post updatedPost = postService.updatePost(id, content);
            return ResponseEntity.ok(updatedPost);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar publicación: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar publicación: " + e.getMessage(), e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postService.searchPosts(query, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsPage.getContent());
        response.put("hasMore", postsPage.hasNext());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("currentPage", page);
        response.put("totalElements", postsPage.getTotalElements());
        
        return ResponseEntity.ok(response);
    }

    private String getCurrentUserId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Aquí necesitaríamos inyectar JwtTokenProvider, pero por simplicidad usaremos el SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();
                return user.getId();
            }
        }
        throw new RuntimeException("Usuario no autenticado");
    }
}

package com.clipers.clipers.controller;

import com.clipers.clipers.entity.Comment;
import com.clipers.clipers.entity.Post;
import com.clipers.clipers.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Post> createPost(@RequestBody Map<String, Object> request) {
        try {
            String userId = getCurrentUserId();
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
    public ResponseEntity<Void> likePost(@PathVariable String id) {
        try {
            String userId = getCurrentUserId();
            postService.toggleLike(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException("Error al dar like: " + e.getMessage(), e);
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            String userId = getCurrentUserId();
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

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "user-" + Math.abs(auth.getName().hashCode());
    }
}

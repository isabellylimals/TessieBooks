package com.isabelly.tessiebooks.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.entity.Post;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.service.PostService;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final PostService postService;

    public FeedController(PostService postService) {
        this.postService = postService;
    }

    // GET /feed - retorna todos os posts
    @GetMapping
    public ResponseEntity<?> getFeed() {
        List<Post> posts = postService.getFeed();
        return ResponseEntity.ok(posts);
    }

   
    @PostMapping
    public ResponseEntity<?> createPost(Authentication auth, @RequestBody Map<String, String> req) {
        User user = (User) auth.getPrincipal();
        String content = req.get("content");

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Conteúdo não pode estar vazio");
        }

        Post post = postService.createPost(user, content);
        return ResponseEntity.ok(post);
    }

   
@DeleteMapping("/{id}")
public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication auth) {
    User user = (User) auth.getPrincipal();
    Post post = postService.getPost(id); // Buscar primeiro
    if (!post.getUser().getId().equals(user.getId())) {
        return ResponseEntity.status(403).body("Você não pode deletar posts de outros usuários");
    }
    postService.deletePost(id);
    return ResponseEntity.ok("Post deletado com sucesso");
}
}

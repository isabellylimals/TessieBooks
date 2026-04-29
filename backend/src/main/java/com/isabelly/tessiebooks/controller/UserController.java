package com.isabelly.tessiebooks.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.isabelly.tessiebooks.entity.Review;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.ReviewRepository;
import com.isabelly.tessiebooks.repository.UserRepository;
import com.isabelly.tessiebooks.service.UserService;

import jakarta.transaction.Transactional;
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public UserController(UserService userService, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    // LISTAR TODOS OS USUÁRIOS (público)
    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

@GetMapping("/me")

public ResponseEntity<?> getMe(Authentication auth) {
    // 1. Verificar autenticação
    if (auth == null || !(auth.getPrincipal() instanceof User)) {
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }
    
    // 2. Pegar o usuário autenticado
    User user = (User) auth.getPrincipal();
    
    // 3. Buscar dados atualizados do banco
    User freshUser = userRepository.findById(user.getId()).orElse(null);
    
    if (freshUser == null) {
        return ResponseEntity.status(404).body("Usuário não encontrado");
    }
    
    // 4. Retornar apenas dados básicos (sem listas)
    Map<String, Object> profile = new HashMap<>();
    profile.put("id", freshUser.getId());
    profile.put("name", freshUser.getName());
    profile.put("email", freshUser.getEmail());
    profile.put("bio", freshUser.getBio() == null ? "" : freshUser.getBio());
    profile.put("profileImage", freshUser.getProfileImage() == null ? "" : freshUser.getProfileImage());
    profile.put("joinDate", freshUser.getJoinDate() == null ? null : freshUser.getJoinDate().toString());
    
    return ResponseEntity.ok(profile);
}
    // SEGUIR OUTRO USUÁRIO
    @PostMapping("/{id}/follow")
public ResponseEntity<?> followUser(@PathVariable Long id, Authentication auth) {
    User me = (User) auth.getPrincipal();
    
    if (me.getId().equals(id)) {
        return ResponseEntity.badRequest().body("Você não pode seguir a si mesmo");
    }
    
    try {
        userService.follow(me.getId(), id);
        return ResponseEntity.ok("Seguido com sucesso");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    // DEIXAR DE SEGUIR
    @PostMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable Long id, Authentication auth) {
        User me = (User) auth.getPrincipal();
        try {
            userService.unfollow(me.getId(), id);
            return ResponseEntity.ok("Deixou de seguir");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ---- GET /users/me ----
@PutMapping("/me/profile-image")
public ResponseEntity<?> updateProfileImage(Authentication auth, @RequestBody Map<String, String> body) {
    User me = (User) auth.getPrincipal();
    String imageUrl = body.get("profileImage");
    
    if (imageUrl == null || imageUrl.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("URL da imagem é obrigatória");
    }
    
    me.setProfileImage(imageUrl);
    userRepository.save(me);
    
    return ResponseEntity.ok(Map.of("profileImage", imageUrl));
}


    // ---- GET /users/{id} ----
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

 @PutMapping("/me")
public ResponseEntity<?> updateProfile(Authentication auth, @RequestBody Map<String, String> updates) {
    User me = (User) auth.getPrincipal();
    
    // Validar se o nome está sendo alterado e se já existe
    if (updates.containsKey("name")) {
        String newName = updates.get("name");
        User existingUser = userRepository.findByName(newName).orElse(null);
        
        if (existingUser != null && !existingUser.getId().equals(me.getId())) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Este nome de usuário já está em uso. Escolha outro.",
                "success", false
            ));
        }
    }
    
    // Validar se o email está sendo alterado e se já existe
    if (updates.containsKey("email")) {
        String newEmail = updates.get("email");
        User existingUser = userRepository.findByEmail(newEmail).orElse(null);
        
        if (existingUser != null && !existingUser.getId().equals(me.getId())) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Este email já está cadastrado.",
                "success", false
            ));
        }
    }
    
    User updated = userService.updateProfile(me.getId(), updates);
    return ResponseEntity.ok(updated);
}
// ---- GET /users/{id}/followers ----
@GetMapping("/{id}/followers")
public ResponseEntity<?> getFollowers(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getFollowers(id));
}

// ---- GET /users/{id}/following ----
@GetMapping("/{id}/following")
public ResponseEntity<?> getFollowing(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getFollowing(id));
}

@GetMapping("/me/stats")
public ResponseEntity<?> getUserStats(Authentication auth) {
    User user = (User) auth.getPrincipal();
    
    User freshUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    
    long reviewsCount = reviewRepository.countByUserId(user.getId());
    
    // Calcular total de likes percorrendo as resenhas
    List<Review> userReviews = reviewRepository.findByUserId(user.getId());
    long totalLikes = 0;
    for (Review review : userReviews) {
        if (review.getLikes() != null) {
            totalLikes += review.getLikes().size();
        }
    }
    
    Map<String, Object> stats = new HashMap<>();
    stats.put("followers", freshUser.getFollowersCount());
    stats.put("following", freshUser.getFollowingCount());
    stats.put("booksRead", freshUser.getTotalBooksRead() != null ? freshUser.getTotalBooksRead() : 0);
    stats.put("pagesRead", freshUser.getTotalPagesRead() != null ? freshUser.getTotalPagesRead() : 0);
    stats.put("reviewsCount", reviewsCount);
    stats.put("totalLikes", totalLikes);
    stats.put("joinDate", freshUser.getJoinDate());
    
    return ResponseEntity.ok(stats);
}


// ---- Favoritos ----
@PostMapping("/me/favorites/{bookId}")
public ResponseEntity<?> addFavorite(@PathVariable Long bookId, Authentication auth) {
    User user = (User) auth.getPrincipal();
    userService.addFavorite(user.getId(), bookId);
    return ResponseEntity.ok(Map.of("message", "Livro adicionado aos favoritos"));
}

@DeleteMapping("/me/favorites/{bookId}")
public ResponseEntity<?> removeFavorite(@PathVariable Long bookId, Authentication auth) {
    User user = (User) auth.getPrincipal();
    userService.removeFavorite(user.getId(), bookId);
    return ResponseEntity.ok(Map.of("message", "Livro removido dos favoritos"));
}

@GetMapping("/me/favorites")
public ResponseEntity<?> getMyFavorites(Authentication auth) {
    User user = (User) auth.getPrincipal();
    return ResponseEntity.ok(userService.getUserFavorites(user.getId()));
}

@GetMapping("/{userId}/favorites")
public ResponseEntity<?> getUserFavorites(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getUserFavorites(userId));
}

@GetMapping("/me/favorites/{bookId}/check")
public ResponseEntity<?> isFavorite(@PathVariable Long bookId, Authentication auth) {
    User user = (User) auth.getPrincipal();
    boolean isFav = userService.isFavorite(user.getId(), bookId);
    return ResponseEntity.ok(Map.of("favorite", isFav));
}
}

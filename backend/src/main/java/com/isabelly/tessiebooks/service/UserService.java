
package com.isabelly.tessiebooks.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.dto.book.BookResponse;
import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;  // ← precisa ter!

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void follow(Long userId, Long targetId) {
        if (userId.equals(targetId)) throw new RuntimeException("Não é possível seguir a si mesmo");
        User me = getById(userId);
        User target = getById(targetId);
        if (!me.getFollowing().contains(target)) {
            me.getFollowing().add(target);
            userRepository.save(me);
        }
    }

    public void unfollow(Long userId, Long targetId) {
        User me = getById(userId);
        User target = getById(targetId);
        if (me.getFollowing().contains(target)) {
            me.getFollowing().remove(target);
            userRepository.save(me);
        }
    }

    public User updateProfile(Long id, Map<String, String> updates) {
        User user = getById(id);
        
        if (updates.containsKey("name")) {
            user.setName(updates.get("name"));
        }
        if (updates.containsKey("bio")) {
            user.setBio(updates.get("bio"));
        }
        if (updates.containsKey("profileImage")) {
            user.setProfileImage(updates.get("profileImage"));
        }
        if (updates.containsKey("email")) {
            user.setEmail(updates.get("email"));
        }
        
        return userRepository.save(user);
    }

    public List<User> getFollowers(Long userId) {
        User user = getById(userId);
        return user.getFollowers();
    }

    public List<User> getFollowing(Long userId) {
        User user = getById(userId);
        return user.getFollowing();
    }
    
    public boolean isNameTaken(String name, Long excludeUserId) {
        return userRepository.findByName(name)
            .map(user -> !user.getId().equals(excludeUserId))
            .orElse(false);
    }

    // ---- Favoritos ----
public void addFavorite(Long userId, Long bookId) {
    User user = getById(userId);
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    
    if (!user.getFavoriteBooks().contains(book)) {
        user.getFavoriteBooks().add(book);
        userRepository.save(user);
    }
}

public void removeFavorite(Long userId, Long bookId) {
    User user = getById(userId);
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    
    user.getFavoriteBooks().remove(book);
    userRepository.save(user);
}

public List<BookResponse> getUserFavorites(Long userId) {
    User user = getById(userId);
    return user.getFavoriteBooks().stream()
        .map(book -> {
            BookResponse dto = new BookResponse();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setAuthor(book.getAuthor());
            dto.setDescription(book.getDescription());
            dto.setGenre(book.getGenre());
            dto.setCoverUrl(book.getCoverImageUrl()); // Mapear coverImageUrl para coverUrl
            dto.setPublicationYear(book.getPublicationYear());
            dto.setPages(book.getPages());
            return dto;
        })
        .collect(Collectors.toList());
}

public boolean isFavorite(Long userId, Long bookId) {
    User user = getById(userId);
    return user.getFavoriteBooks().stream()
        .anyMatch(book -> book.getId().equals(bookId));
}
}

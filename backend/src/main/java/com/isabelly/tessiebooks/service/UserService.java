package com.isabelly.tessiebooks.service;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

  
public User getById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
}

public java.util.List<User> getAllUsers() {
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
    
    return userRepository.save(user);
}

// Buscar seguidores de um usuário
public List<User> getFollowers(Long userId) {
    User user = getById(userId);
    return user.getFollowers();
}

// Buscar quem um usuário segue
public List<User> getFollowing(Long userId) {
    User user = getById(userId);
    return user.getFollowing();
}

}

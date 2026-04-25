package com.isabelly.tessiebooks.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/debug")
public class ResetPasswordController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(
            @RequestParam String email, 
            @RequestParam String newPassword,
            HttpServletRequest request) {
        

        String ip = request.getRemoteAddr();
        if (!ip.equals("127.0.0.1") && !ip.equals("0:0:0:0:0:0:0:1") && !ip.equals("localhost")) {
            return ResponseEntity.status(403).body("Acesso negado. Endpoint apenas para desenvolvimento.");
        }
        
 
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Nova senha deve ter no mínimo 6 caracteres");
        }
        

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email é obrigatório");
        }
        
    
        Optional<User> opt = userRepository.findByEmail(email);

        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }


        User u = opt.get();
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}
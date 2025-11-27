package com.isabelly.tessiebooks.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.UserRepository;

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
    public String reset(@RequestParam String email, @RequestParam String newPassword) {

        Optional<User> opt = userRepository.findByEmail(email);

        if (opt.isEmpty()) {
            return "Usuário não encontrado";
        }

        User u = opt.get(); // pega o User real
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);

        return "Senha redefinida com sucesso!";
    }
}

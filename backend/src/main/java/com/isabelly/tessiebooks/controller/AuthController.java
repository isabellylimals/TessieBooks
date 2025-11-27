package com.isabelly.tessiebooks.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isabelly.tessiebooks.config.JwtService;
import com.isabelly.tessiebooks.dto.ApiResponse;
import com.isabelly.tessiebooks.dto.auth.LoginRequest;
import com.isabelly.tessiebooks.dto.auth.LoginResponse;
import com.isabelly.tessiebooks.dto.auth.RegisterRequest;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder encoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Email já registrado.", false));
        }

        if (req.getName() != null && userRepository.findByName(req.getName()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Nome de usuário já em uso.", false));
        }

        try {
            User u = new User();
            u.setName(req.getName());
            u.setEmail(req.getEmail());
            u.setPassword(encoder.encode(req.getPassword()));

            userRepository.save(u);

            return ResponseEntity.ok(new ApiResponse("Usuário registrado com sucesso!", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Erro ao registrar: " + e.getMessage(), false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElse(null);

        if (user == null || !encoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(new ApiResponse("Credenciais inválidas.", false));
        }

        String token = jwtService.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse(token, user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(response);
    }
}


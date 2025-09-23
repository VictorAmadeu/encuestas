package com.acme.encuestas.service;

import com.acme.encuestas.dto.AuthRequest;
import com.acme.encuestas.dto.AuthResponse;
import com.acme.encuestas.dto.RegisterRequest;
import com.acme.encuestas.model.User;
import com.acme.encuestas.repository.UserRepository;
import com.acme.encuestas.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // comprobar si existe email
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese email.");
        }

        // normaliza rol (default USER si viene nulo/vacío)
        String role = (request.role() == null || request.role().isBlank())
                ? "USER"
                : request.role().trim().toUpperCase();

        User user = User.builder()
                // NO seteamos id: lo genera JPA
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(role)
                .enabled(true)
                .build();

        user = userRepository.save(user); // aquí ya tendrá id
        String token = jwtUtil.generateToken(user.getId().toString(), user.getRole());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        boolean ok = user.isEnabled() &&
                passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!ok) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user.getId().toString(), user.getRole());
        return new AuthResponse(token);
    }
}

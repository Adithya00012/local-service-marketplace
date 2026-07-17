package com.localservice.backend.controller;

import com.localservice.backend.dto.AuthResponseDTO;
import com.localservice.backend.dto.LoginRequestDTO;
import com.localservice.backend.dto.UserRequestDTO;
import com.localservice.backend.model.User;
import com.localservice.backend.repository.UserRepository;
import com.localservice.backend.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.localservice.backend.exception.InvalidCredentialsException;
import com.localservice.backend.exception.DuplicateEmailException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
        throw new DuplicateEmailException("An account with this email already exists");
    }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getEmail());

        AuthResponseDTO response = new AuthResponseDTO(
                token, saved.getEmail(), saved.getName(), saved.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        AuthResponseDTO response = new AuthResponseDTO(
                token, user.getEmail(), user.getName(), user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
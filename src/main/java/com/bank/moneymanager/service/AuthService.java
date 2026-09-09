package com.bank.moneymanager.service;

import com.bank.moneymanager.dto.AuthResponseDTO;
import com.bank.moneymanager.dto.LoginRequestDTO;
import com.bank.moneymanager.dto.RegisterRequestDTO;
import com.bank.moneymanager.entity.User;
import com.bank.moneymanager.repository.UserRepository;
import com.bank.moneymanager.security.JwtService;
import com.bank.moneymanager.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto, String clientIp) {
        if (userRepository.existsByUsername(dto.getUsername().trim())) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (userRepository.existsByEmail(dto.getEmail().trim())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim().toLowerCase());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName().trim());
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        auditService.logAction(savedUser.getId(), "USER_REGISTERED", "New user onboarded", clientIp);

        UserPrincipal principal = new UserPrincipal(savedUser);
        String token = jwtService.generateToken(principal);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto, String clientIp) {
        User user = userRepository.findByUsername(dto.getUsernameOrEmail().trim().toLowerCase())
                .or(() -> userRepository.findByEmail(dto.getUsernameOrEmail().trim().toLowerCase()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), dto.getPassword())
        );

        auditService.logAction(user.getId(), "USER_LOGIN_SUCCESS", "Authenticated via credentials", clientIp);

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
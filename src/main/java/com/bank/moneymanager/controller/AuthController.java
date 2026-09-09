package com.bank.moneymanager.controller;

import com.bank.moneymanager.dto.AuthResponseDTO;
import com.bank.moneymanager.dto.LoginRequestDTO;
import com.bank.moneymanager.dto.RegisterRequestDTO;
import com.bank.moneymanager.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO requestDTO,
            HttpServletRequest request) {

        String clientIp = extractClientIp(request);
        AuthResponseDTO response = authService.register(requestDTO, clientIp);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDTO,
            HttpServletRequest request) {

        String clientIp = extractClientIp(request);
        AuthResponseDTO response = authService.login(requestDTO, clientIp);
        return ResponseEntity.ok(response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
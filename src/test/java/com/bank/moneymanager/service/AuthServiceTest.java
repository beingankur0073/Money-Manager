package com.bank.moneymanager.service;

import com.bank.moneymanager.dto.AuthResponseDTO;
import com.bank.moneymanager.dto.RegisterRequestDTO;
import com.bank.moneymanager.entity.User;
import com.bank.moneymanager.repository.UserRepository;
import com.bank.moneymanager.security.JwtService;
import com.bank.moneymanager.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_NewUser_HashesPasswordAndSaves() {
        // 1. Arrange (Set up the fake registration request)
        RegisterRequestDTO request = new RegisterRequestDTO(
                "newuser",
                "user@example.com",
                "PlaintextPassword123",
                "John Doe"
        );

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);

        // Train the fake password encoder to return a hashed string
        when(passwordEncoder.encode("PlaintextPassword123")).thenReturn("HashedBcryptStringXYZ");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("user@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(UserPrincipal.class))).thenReturn("mock-jwt-token");

        // 2. Act (Execute registration)
        AuthResponseDTO response = authService.register(request, "192.168.1.1");

        // 3. Assert (Verify the token generation and password encryption)
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());

        // Use ArgumentCaptor to intercept the User object right as it hits the repository
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("HashedBcryptStringXYZ", capturedUser.getPasswordHash(), "The password must be hashed before saving!");
        assertNotEquals("PlaintextPassword123", capturedUser.getPasswordHash(), "CRITICAL: Stored plain text password!");
    }

    @Test
    void register_ExistingUsername_ThrowsException() {
        // 1. Arrange (Simulate a username collision)
        RegisterRequestDTO request = new RegisterRequestDTO(
                "existinguser",
                "user@example.com",
                "password",
                "John Doe"
        );
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // 2 & 3. Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request, "192.168.1.1"));

        assertEquals("Username already in use", exception.getMessage());

        // Prove we aborted the transaction before saving or hashing anything
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
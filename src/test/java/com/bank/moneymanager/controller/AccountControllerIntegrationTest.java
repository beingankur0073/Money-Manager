package com.bank.moneymanager.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.moneymanager.dto.AccountRequestDTO;
import com.bank.moneymanager.entity.User;
import com.bank.moneymanager.enums.AccountType;
import com.bank.moneymanager.security.UserPrincipal;
import com.bank.moneymanager.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void createAccount_InvalidBankingDetails_Returns400WithValidationErrors() throws Exception {
        AccountRequestDTO badRequest = new AccountRequestDTO(
                "",                         // accountName (blank)
                "",                         // bankName (blank)
                "123",                      // accountNumber (< 9 digits)
                "INVALID_IFSC",             // ifscCode (invalid format)
                null,                       // bankLogoUrl
                AccountType.SAVINGS,        // accountType
                new BigDecimal("-100.00"),  // initialBalance (negative)
                "INR"                       // currency
        );

        // Construct User entity matching the actual User fields
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("ankur_dev");
        mockUser.setPasswordHash("$2a$10$hashedPasswordHere");
        mockUser.setFullName("Ankur Mukherjee");
        mockUser.setEmail("ankur@example.com");
        mockUser.setRole("ROLE_USER");

        UserPrincipal mockPrincipal = new UserPrincipal(mockUser);

        String jsonPayload = objectMapper.writeValueAsString(badRequest);

        mockMvc.perform(post("/api/accounts")
                        .with(user(mockPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.accountName").exists())
                .andExpect(jsonPath("$.validationErrors.bankName").exists())
                .andExpect(jsonPath("$.validationErrors.accountNumber").exists())
                .andExpect(jsonPath("$.validationErrors.ifscCode").exists())
                .andExpect(jsonPath("$.validationErrors.initialBalance").exists());
    }
}
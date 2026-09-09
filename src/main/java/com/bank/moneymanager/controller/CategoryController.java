package com.bank.moneymanager.controller;

import com.bank.moneymanager.dto.CategoryRequestDTO;
import com.bank.moneymanager.dto.CategoryResponseDTO;
import com.bank.moneymanager.security.UserPrincipal;
import com.bank.moneymanager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<CategoryResponseDTO> categories = categoryService.getCategoriesForUser(principal.getId());
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCustomCategory(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {

        CategoryResponseDTO response = categoryService.createCustomCategory(principal.getId(), requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomCategory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {

        categoryService.deleteCustomCategory(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
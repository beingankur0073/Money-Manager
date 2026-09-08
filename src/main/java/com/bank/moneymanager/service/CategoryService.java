package com.bank.moneymanager.service;

import com.bank.moneymanager.dto.CategoryRequestDTO;
import com.bank.moneymanager.dto.CategoryResponseDTO;
import com.bank.moneymanager.entity.Category;
import com.bank.moneymanager.entity.User;
import com.bank.moneymanager.exception.ResourceNotFoundException;
import com.bank.moneymanager.exception.UnauthorizedAccessException;
import com.bank.moneymanager.repository.CategoryRepository;
import com.bank.moneymanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CategoryResponseDTO createCustomCategory(Long userId, CategoryRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Category category = new Category();
        category.setUser(user);
        category.setName(dto.getName().trim());
        category.setType(dto.getType());

        Category savedCategory = categoryRepository.save(category);
        return mapToDTO(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getCategoriesForUser(Long userId) {
        return categoryRepository.findByUserIdOrUserIdIsNull(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional
    public void deleteCustomCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        if (category.getUser() == null) {
            throw new UnauthorizedAccessException("System default categories cannot be deleted.");
        }

        if (!category.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this category.");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponseDTO mapToDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .isSystemDefault(category.getUser() == null)
                .createdAt(category.getCreatedAt())
                .build();
    }
}
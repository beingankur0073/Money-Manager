package com.bank.moneymanager.dto;

import com.bank.moneymanager.enums.CategoryType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private CategoryType type;
    private boolean isSystemDefault;
    private LocalDateTime createdAt;
}
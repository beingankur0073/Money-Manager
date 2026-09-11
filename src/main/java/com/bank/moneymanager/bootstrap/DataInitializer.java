package com.bank.moneymanager.bootstrap;

import com.bank.moneymanager.entity.Category;
import com.bank.moneymanager.enums.CategoryType;
import com.bank.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(@NonNull String... args) throws Exception {
        log.info("Checking database for system categories...");

        // Only seed if the category table is entirely empty
        if (categoryRepository.count() == 0) {
            log.info("No categories found. Seeding initial system categories...");

            List<Category> defaultCategories = Arrays.asList(
                    createSystemCategory("Salary", CategoryType.INCOME),
                    createSystemCategory("Investments", CategoryType.INCOME),
                    createSystemCategory("Groceries", CategoryType.EXPENSE),
                    createSystemCategory("Utilities", CategoryType.EXPENSE),
                    createSystemCategory("Rent", CategoryType.EXPENSE),
                    createSystemCategory("Health", CategoryType.EXPENSE),
                    createSystemCategory("Entertainment", CategoryType.EXPENSE)
            );

            categoryRepository.saveAll(defaultCategories);
            log.info("Successfully seeded {} default categories.", defaultCategories.size());
        } else {
            log.info("System categories already exist. Skipping seed process.");
        }
    }

    private Category createSystemCategory(String name, CategoryType type) {
        Category category = new Category();
        category.setName(name);
        category.setType(type); // Now correctly passing the Enum instead of a String
        return category;
    }
}
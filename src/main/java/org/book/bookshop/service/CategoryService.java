package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Category;
import org.book.bookshop.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.findCategoryByName(name);
        return category.stream().findFirst().orElse(null);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}

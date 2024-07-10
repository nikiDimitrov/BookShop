package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoCategoriesException;
import org.book.bookshop.model.Category;
import org.book.bookshop.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.findCategoryByName(name);
        return category.stream().findFirst().orElse(null);
    }

    public Category saveCategory(String categoryName) {
        return categoryRepository.save(new Category(categoryName));
    }

    public List<Category> getAllCategories() throws NoCategoriesException {
        List<Category> categories =  categoryRepository.findAll();

        if(categories.isEmpty()) {
            throw new NoCategoriesException("No categories!");
        }

        return categories;
    }
}

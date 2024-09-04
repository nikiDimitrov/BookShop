package org.book.bookshop.service;

import org.book.bookshop.model.Category;
import org.book.bookshop.repository.CategoryRepository;

import java.util.Optional;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService() {
        this.categoryRepository = new CategoryRepository();
    }

    public Category getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.findCategoryByName(name);
        return category.stream().findFirst().orElse(null);
    }

    public Category saveCategory(String categoryName) {
        return categoryRepository.save(new Category(categoryName));
    }

    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }
}

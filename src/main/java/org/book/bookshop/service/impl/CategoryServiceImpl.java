package org.book.bookshop.service.impl;

import org.book.bookshop.model.Category;
import org.book.bookshop.repository.CategoryRepository;
import org.book.bookshop.service.CategoryService;

import java.util.Optional;

public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Category getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.getCategoryByName(name);
        return category.stream().findFirst().orElse(null);
    }
}

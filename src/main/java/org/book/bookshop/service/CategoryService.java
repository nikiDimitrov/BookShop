package org.book.bookshop.service;

import org.book.bookshop.helpers.Result;
import org.book.bookshop.model.Category;
import org.book.bookshop.repository.CategoryRepository;

import java.sql.SQLException;
import java.util.Optional;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService() {
        this.categoryRepository = new CategoryRepository();
    }

    public Result<Category> getCategoryByName(String name) {
        try {
            Optional<Category> category = categoryRepository.findCategoryByName(name);

            return category
                    .map(Result::success)
                    .orElse(Result.failure("Category not found!"));
        }
        catch (SQLException e) {
            return Result.failure(String.format("Database error while fetching category! %s!", e.getMessage()));
        }
    }

    public Result<Category> saveCategory(String categoryName) {
        try {
            Category category = categoryRepository.save(new Category(categoryName));

            if(category == null) {
                return Result.failure("Couldn't save category.");
            }
            else {
                return Result.success(category);
            }
        }
        catch(SQLException e) {
            return Result.failure(String.format("Database error while saving category. %s!", e.getMessage()));
        }
    }
}

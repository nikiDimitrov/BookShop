package org.book.bookshop.helpers;

import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static User isUserValid(String username, String email, String password) {
        boolean userNameValid = Validator.isUsernameValid(username);
        boolean emailValid = Validator.isEmailValid(email);
        boolean passwordValid = Validator.isPasswordValid(password);

        if(userNameValid || emailValid || passwordValid) {
            return new User(username, email, password);
        }
        else {
            return null;
        }

    }

    public static Book isBookValid(String name, String author, double price, List<Category> categories, int year, int quantity) {
        boolean nameValid = isNameValid(name);
        boolean authorValid = isNameValid(name);
        boolean priceValid = isNumberPositive(price);
        boolean yearValid = isNumberPositive(year);
        boolean quantityValid = isNumberPositive(quantity);
        boolean categoriesValid = true;

        for(Category category : categories) {
            if(!Validator.isCategoryValid(category.getName())) {
                categoriesValid = false;
                break;
            }
        }

        if(nameValid && authorValid
                && priceValid && yearValid
                && quantityValid && categoriesValid) {
            return new Book(name, author, price, categories, year, quantity);
        }
        else {
            return null;
        }
    }

    public static boolean isDouble(String toBeDouble) {
        try {
            Double.parseDouble(toBeDouble);
        }
        catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isInteger(String toBeInteger) {
        try {
            Integer.parseInt(toBeInteger);
        }
        catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    private static boolean isUsernameValid(String username) {
        return !isInputNullOrEmpty(username) && username.length() >= 3 && username.length() <= 30;
    }

    private static boolean isEmailValid(String email) {
        if(isInputNullOrEmpty(email)) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private static boolean isPasswordValid(String password) {

        return !isInputNullOrEmpty(password) && password.length() > 5;
    }

    private static boolean isNameValid(String name) {
        return !isInputNullOrEmpty(name) && name.length() >= 3;
    }

    private static boolean isCategoryValid(String categoryName) {
        String categoryRegex = "^[a-zA-Z]{3,}+$";

        Pattern categoryPattern = Pattern.compile(categoryRegex);
        Matcher categoryMatcher = categoryPattern.matcher(categoryName);

        return categoryMatcher.matches();
    }

    private static boolean isNumberPositive(double number) {
        return number > 0;
    }

    private static boolean isNumberPositive(int number) {
        return number > 0;
    }

    private static boolean isInputNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }
}

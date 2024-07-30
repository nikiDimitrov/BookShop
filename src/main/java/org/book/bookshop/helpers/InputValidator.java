package org.book.bookshop.helpers;

public class InputValidator {

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
}

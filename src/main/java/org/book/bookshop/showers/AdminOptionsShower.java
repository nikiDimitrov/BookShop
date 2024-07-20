package org.book.bookshop.showers;

public class AdminOptionsShower extends OptionsShower{
    public static void showOptions() {
        String[] options = new String[]{
                "Add an employee",
                "Show all users",
                "Add a book",
                "Remove a book",
                "Show all books",
                "Show all orders"
        };
        showOptions(options);
    }
}

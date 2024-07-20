package org.book.bookshop.constants;

public class AdminOptionsShower extends OptionsShower{
    public static void showOptions() {
        String[] options = new String[]{
                "Add an employee",
                "Show all users",
                "Add a book",
                "Remove a book",
                "Show all books",
        };
        showOptions(options);
    }
}

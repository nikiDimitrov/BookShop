package org.book.bookshop.showers;

public class EmployeeOptionsShower extends OptionsShower{
    public static void showOptions() {
        String[] options = {
                "Approve orders",
                "Restock books",
                "Show all books"
        };
        showOptions(options);
    }

}

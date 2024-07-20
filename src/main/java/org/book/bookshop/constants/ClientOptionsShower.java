package org.book.bookshop.constants;

public class ClientOptionsShower extends OptionsShower{
    public static void showOptions() {
        String[] options = new String[]{
                "Order a book",
                "View your orders"
        };
        showOptions(options);
    }
}

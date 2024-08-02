package org.book.bookshop.showers;

public abstract class OptionsShower {
    protected static void showOptions(String[] options) {
        for(int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s\n", i + 1, options[i]);
        }

        System.out.println("0. Exit");
    }

}

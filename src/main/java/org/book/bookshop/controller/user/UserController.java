package org.book.bookshop.controller.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.User;
import org.book.bookshop.view.user.AdminView;
import org.book.bookshop.view.user.LoginView;
import org.book.bookshop.view.user.UserView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public abstract class UserController {

    protected final LoginView loginView;
    protected final ObjectMapper objectMapper;
    protected final BufferedWriter out;
    protected final BufferedReader in;

    protected UserView view;
    protected User user;

    protected static final String SEPARATOR = ", *";

    protected UserController(BufferedWriter out, BufferedReader in) {
        this.loginView = new LoginView();
        this.objectMapper = new ObjectMapper();
        this.out = out;
        this.in = in;
        this.view = new AdminView();
    }

    public abstract int run(User user);

    protected List<Book> getAllBooks() throws IOException {
        try {
            JsonNode request = objectMapper.createObjectNode()
                    .put("action", "SHOW_BOOKS")
                    .put("user", user.getUsername());

            out.write(request.toString() + "\n");
            out.flush();

            JsonNode response = objectMapper.readTree(in.readLine());
            String status = response.get("status").asText();

            if (status.equals("success")) {
                String booksJson = response.get("books").asText();
                return objectMapper.readValue(booksJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Book.class));
            } else {
                view.displayError(response.get("message").asText());
                return null;
            }
        } catch (IOException e) {
            view.displayError("Error while communicating with the server.");
        }

        return null;
    }

}

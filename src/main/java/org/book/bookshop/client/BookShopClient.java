package org.book.bookshop.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.book.bookshop.client.user.AdminController;
import org.book.bookshop.client.user.ClientController;
import org.book.bookshop.client.user.EmployeeController;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.view.user.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class BookShopClient {
    private static final Logger log = LoggerFactory.getLogger(BookShopClient.class);

    private final Socket socket;
    private final BufferedWriter out;
    private final BufferedReader in;
    private final ObjectMapper objectMapper;
    private final LoginView loginView;
    private final AdminController adminController;
    private final ClientController clientController;
    private final EmployeeController employeeController;
    private User user;

    public BookShopClient(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        objectMapper = new ObjectMapper();
        loginView = new LoginView();
        adminController = new AdminController(out, in);
        employeeController = new EmployeeController(out, in);
        clientController = new ClientController(out, in);
    }

    public void start() {
        try {
            System.out.println("Connected to the server.");
            runMainFlow();
        } catch (IOException e) {
            log.error(String.format("Can't start client. %s", e.getMessage()));
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error(String.format("Can't close client. %s", e.getMessage()));
            }
        }
    }

    private void runMainFlow() throws IOException {
        while (user == null) {
            String action = loginView.generalPrompt();

            switch (action) {
                case "login":
                    loginUser();
                    break;
                case "register":
                    registerUser();
                    break;
                default:
                    System.out.println("Invalid option. Type 'login' or 'register'.");
            }
        }

        int input = handleRoleSpecificOptions();

        while (input != 0) {
            input = handleRoleSpecificOptions();
        }
    }

    private void loginUser() throws IOException {
        JsonNode loginRequest = loginView.loginPrompts();

        out.write(loginRequest.toString() + "\n");
        out.flush();

        String responseLine = in.readLine();
        JsonNode response = objectMapper.readTree(responseLine);

        if (response.get("status").asText().equals("success")) {
            System.out.println("Login successful!");

            getUserDetails(response);

        } else {
            System.out.println("Login failed: " + response.get("message").asText());
        }
    }

    private void registerUser() throws IOException {
        JsonNode registerRequest = loginView.registerPrompts();

        out.write(registerRequest.toString() + "\n");
        out.flush();

        String responseLine = in.readLine();
        JsonNode response = objectMapper.readTree(responseLine);

        if (response.get("status").asText().equals("success")) {
            getUserDetails(response);

            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed: " + response.get("message").asText());
        }
    }

    private void getUserDetails(JsonNode response) {
        UUID id = UUID.fromString(response.get("id").asText());
        String username = response.get("username").asText();
        String email = response.get("email").asText();
        String password = response.get("password").asText();
        Role role = Role.valueOf(response.get("role").asText());

        user = new User(username, email, password);
        user.setId(id);
        user.setRole(role);
    }


    private int handleRoleSpecificOptions() {
        return switch (user.getRole()) {
            case ADMIN -> handleAdminOptions();
            case CLIENT -> handleClientOptions();
            case EMPLOYEE -> handleEmployeeOptions();
        };

    }

    private int handleAdminOptions() {
        return adminController.run(user);
    }

    private int handleClientOptions() {
        return clientController.run(user);
    }

    private int handleEmployeeOptions() {
        return employeeController.run(user);
    }

    public static void main(String[] args) {
        try {
            String serverIP = "87.119.114.56";
            int port = 3333;
            log.info("Client is going to connect to server with IP {} and port {}.", serverIP, port);

            BookShopClient client = new BookShopClient(serverIP, port);
            client.start();

        } catch (IOException e) {
            log.error("Can't start client! {}!", e.getMessage());
        }
    }
}

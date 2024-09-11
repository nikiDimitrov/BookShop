package org.book.bookshop.server;

import org.book.bookshop.helpers.DatabaseCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BookShopServer {
    private static final Logger log = LoggerFactory.getLogger(BookShopServer.class);
    private final ServerSocket serverSocket;

    public BookShopServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        System.out.println("Server started! Waiting for client connections...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                new ClientHandler(clientSocket).start();
            }
            catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            DatabaseCreator creator = new DatabaseCreator();
            creator.createTableFromScript("tables.sql");

            BookShopServer server = new BookShopServer(3333);
            
            server.start();
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }


}

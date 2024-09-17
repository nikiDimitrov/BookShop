package org.book.bookshop.server;

import org.book.bookshop.helpers.DatabaseCreator;
import org.book.bookshop.helpers.IPGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BookShopServer {
    private final List<ClientHandler> connectedClients = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(BookShopServer.class);
    private final ServerSocket serverSocket;

    public BookShopServer(int port) throws IOException {
        serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
    }

    public void start() {
        System.out.println("Server started! Waiting for client connections...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                ClientHandler clientHandler = new ClientHandler(clientSocket, connectedClients);
                connectedClients.add(clientHandler);
                clientHandler.start();

            }
            catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            String publicIP = IPGetter.getIp();

            if(publicIP != null && publicIP.isEmpty()) {
                System.out.println("Can't get IP address... Exiting...");
            }
            else {
                System.out.println("Server public IP: " + publicIP);
                DatabaseCreator creator = new DatabaseCreator();
                boolean success = creator.createTableFromScript("tables.sql");

                if(success) {
                    BookShopServer server = new BookShopServer(3333);
                    server.start();
                }
                else {
                    System.out.println("Exiting...");
                }
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }


}

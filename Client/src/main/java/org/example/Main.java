package org.example;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    /**
     * The main entry point of the application. This method initializes the client
     * with the specified server address and port, and establishes a connection to
     * the server to facilitate bidirectional communication. Handles any
     * exceptions that may occur during the connection process.
     *
     * @param args the command-line arguments passed to the application. These are
     *             not used within this method.
     */
    public static void main(String[] args) {

        Client client = new Client(SERVER_ADDRESS, SERVER_PORT);
        try {
            client.connect();
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println("Failed");
        }
    }
}
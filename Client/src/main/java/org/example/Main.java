package org.example;

import org.example.client.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    /**
     * The main entry point of the application. This method initializes the client with the specified
     * server address and port and establishes a connection to the server to facilitate bidirectional
     * communication. Handles any exceptions that may occur during the connection process.
     *
     * @param args the command-line arguments passed to the application. These are not used within
     *             this method.
     */
    public static void main(String[] args) {

        ChatClient client = new ChatClient(SERVER_ADDRESS, SERVER_PORT);
        try {
            client.connect();
        } catch (Exception e) {
            logger.error("Client failed", e);
        }
    }
}

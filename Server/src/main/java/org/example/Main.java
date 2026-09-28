package org.example;

import org.example.server.ChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int SERVER_PORT = 12345;

    /**
     * The entry point of the application. This method initializes and starts the server on a
     * predefined port, handling potential I/O exceptions that may occur during startup.
     *
     * @param args Command-line arguments passed to the program. These are currently not used.
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer(SERVER_PORT);
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Server failed", e);
        }
    }
}
package org.example;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

// Main du client
public class Main {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 12345;
        Client client = new Client(serverAddress, serverPort);
        try {
            client.connect();
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println("Failed");
        }
        // System.out.println("Client terminé");
    }
}
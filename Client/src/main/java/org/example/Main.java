package org.example;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {

        Client client = new Client(SERVER_ADDRESS, SERVER_PORT);
        try {
            client.connect();
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println("Failed");
        }
    }
}
package org.example;

import java.io.IOException;

public class Main {

    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Server server = new Server(SERVER_PORT);
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("error");
        }
    }
}
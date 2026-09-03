package org.example;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class Main {

    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        // Port du serveur
        Server server = new Server(SERVER_PORT);
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("error");
        }
        // System.out.println("Code inutile");
        // ArrayList<String> temp = new ArrayList<>();
    }
}
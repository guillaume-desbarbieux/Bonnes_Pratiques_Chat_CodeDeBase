package org.example;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class Main {
    public static void main(String[] args) {
        // Port du serveur
        int serverPort = 12345;
        Server server = new Server(serverPort);
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("error");
        }
        // System.out.println("Code inutile");
        // ArrayList<String> temp = new ArrayList<>();
    }
}
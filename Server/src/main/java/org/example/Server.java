package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int serverPort;
    private final List<ClientHandler> clientHandlerList = new ArrayList<>();
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private final List<String> history = new ArrayList<>();
    private int lastClientId = 0;
    private final int MAX_HISTORY_LENGTH = 100;
    private final String HOSTNAME = "0.0.0.0";

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(HOSTNAME, serverPort));
        isRunning = true;
        System.out.println("Chat server started on port " + serverPort);

        while (isRunning) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clientHandlerList.add(clientHandler);
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    public void stop() throws IOException {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    class ClientHandler implements Runnable {
        Socket socket;
        PrintWriter out;
        String clientName;
        private int clientId;

        public ClientHandler(Socket socket, Server srv) {
            this.socket = socket;
            this.clientId = lastClientId++;
        }

        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                OutputStream outputStream = socket.getOutputStream();
                out = new PrintWriter(new OutputStreamWriter(outputStream), true);

                out.println("Enter your name: ");
                clientName = bufferedReader.readLine();

                for (String s : history)
                    out.println(s);

                sendMessage(clientName + " has joined the chat.");

                String input;
                while ((input = bufferedReader.readLine()) != null)
                    sendMessage(clientName + ": " + input);

                sendMessage(clientName + " has left the chat.");

            } catch (IOException e) {
                System.out.println("Client error");
            }
        }

        private void sendMessage(String message) {
            System.out.println(message);
            history.add(message);
            cleanHistory();
            broadcastMessage(message);
        }

        private void broadcastMessage(String message) {
            for (ClientHandler c : clientHandlerList) {
                if (c != this && c.clientName != null) {
                    try {
                        c.out.println(message);
                    } catch (Exception e) {
                        // client déconnecté ?
                    }
                }
            }
        }
    }

    private void cleanHistory() {
        if (history.size() > MAX_HISTORY_LENGTH) {
            history.remove(0);
        }
    }
}
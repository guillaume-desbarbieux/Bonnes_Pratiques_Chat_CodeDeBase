package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import com.google.common.collect.Lists;

public class Server {
    private int serverP;
    private List<ClientHandler> clientHandlerList = new ArrayList<>();
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private List<String> history = new ArrayList<>();
    private int lastClientId = 0;

    public Server(int serverPort) {
        this.serverP = serverPort;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("0.0.0.0", serverP));
        isRunning = true;
        System.out.println("Chat server started on port " + serverP);

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

    // Classe interne pour gérer chaque client
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

                for (int i = 0; i < history.size(); i++) {
                    out.println(history.get(i));
                }

                String message = clientName + " has joined the chat.";
                System.out.println(message);
                history.add(message);
                if (history.size() > 100) {
                    history.remove(0);
                }
                for (int i = 0; i < clientHandlerList.size(); i++) {
                    ClientHandler clientHandler = clientHandlerList.get(i);
                    if (clientHandler != this && clientHandler.clientName != null) {
                        try {
                            clientHandler.out.println(message);
                        } catch (Exception e) {
                            // client déconnecté ?
                        }
                    }
                }

                String input;
                while ((input = bufferedReader.readLine()) != null) {
                    message = clientName + ": " + input;
                    System.out.println(message);
                    history.add(message);
                    if (history.size() > 100) {
                        history.remove(0);
                    }
                    for (int i = 0; i < clientHandlerList.size(); i++) {
                        ClientHandler c = clientHandlerList.get(i);
                        if (c != this && c.clientName != null) {
                            try {
                                c.out.println(message);
                            } catch (Exception e) {
                                // client déconnecté ?
                            }
                        }
                    }
                }

                String exitMessage = clientName + " has left the chat.";
                System.out.println(exitMessage);
                history.add(exitMessage);
                if (history.size() > 100) {
                    history.remove(0);
                }
                for (int i = 0; i < clientHandlerList.size(); i++) {
                    ClientHandler c = clientHandlerList.get(i);
                    if (c != this && c.clientName != null) {
                        try {
                            c.out.println(exitMessage);
                        } catch (Exception e) {
                            // client déconnecté ?
                        }
                    }
                }

            } catch (IOException e) {
                System.out.println("Client error");
            }
        }
    }
}
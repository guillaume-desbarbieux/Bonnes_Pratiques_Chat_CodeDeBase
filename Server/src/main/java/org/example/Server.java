package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private static final int MAX_MESSAGE_LENGTH = 1000;
  public static final int MAX_CLIENT_NAME_LENGTH = 20;
  private final int serverPort;
  private final List<ClientHandler> clientHandlerList = new ArrayList<>();
  private ServerSocket serverSocket;
  private boolean isRunning = false;
  private final List<String> history = new ArrayList<>();
  private int lastClientId = 0;
  private final int MAX_HISTORY_LENGTH = 100;
  private final String HOSTNAME = "0.0.0.0";

  /**
   * Constructs a new Server instance that listens on the specified port.
   *
   * @param serverPort the port number on which the server will listen for incoming connections
   */
  public Server(int serverPort) {
    this.serverPort = serverPort;
  }

  /**
   * Starts the chat server, allowing it to accept client connections and handle communication
   * between them. This method initializes a server socket, binds it
   */
  public void start() throws IOException {
    serverSocket = new ServerSocket();

    try {
      serverSocket.bind(new InetSocketAddress(HOSTNAME, serverPort));
      isRunning = true;
      System.out.println("Chat server started on port " + serverPort);

      while (isRunning) {
        try {
          Socket clientSocket = serverSocket.accept();

          ClientHandler clientHandler = new ClientHandler(clientSocket, this);
          clientHandlerList.add(clientHandler);
          Thread thread = new Thread(clientHandler);
          thread.start();
        } catch (IOException e) {
          logger.error("Error while accepting client connection", e);
        }
      }
    } finally {
      stop();
    }
  }

  /**
   * Stops the server, halting its operation and closing the server socket if it is open. This
   * method ensures that no further client connections are accepted and releases any currently
   * active server socket resources.
   *
   * @throws IOException if an I/O error occurs while closing the server socket.
   */
  public void stop() throws IOException {
    isRunning = false;

    if (serverSocket != null && !serverSocket.isClosed()) {
      serverSocket.close();
    }
  }

  /**
   * Handles communication with a single client in a multi-client chat server. This class is
   * responsible for receiving messages from the client, broadcasting messages to other clients, and
   * managing client-specific state such as name and ID. Each instance of this class runs on its own
   * thread, allowing simultaneous communication with multiple clients.
   */
  class ClientHandler implements Runnable {
    Socket socket;
    PrintWriter out;
    String clientName;
    private final int clientId;

    /**
     * Constructs a ClientHandler instance to manage communication with a single client. This
     * constructor initializes the client-specific socket and assigns a unique client ID.
     *
     * @param socket the Socket object representing the connection to the client
     * @param server the Server instance that manages this ClientHandler
     */
    public ClientHandler(Socket socket, Server server) {
      this.socket = socket;
      this.clientId = lastClientId++;
    }

    /**
     * Handles the main execution logic for the client communication thread. This method listens for
     * client input, handles messages, and performs actions such as broadcasting messages and
     * updating client state. It also manages the connection lifecycle from the client's
     * perspective, including joining and leaving the chat.
     *
     * <p>Key steps include: - Initializing input and output streams for communication. - Prompting
     * the client to enter a name and broadcasting their arrival. - Sending the chat history to the
     * newly joined client. - Continuously reading and broadcasting client messages until
     * disconnection. - Handling cleanup actions upon client disconnection.
     *
     * <p>Exception Handling: - Captures and logs `IOException` to handle communication errors.
     */
    public void run() {
      try {
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        OutputStream outputStream = socket.getOutputStream();
        out = new PrintWriter(new OutputStreamWriter(outputStream), true);

        out.println("Enter your name: ");
        clientName = bufferedReader.readLine();

        while (clientName == null
            || clientName.isBlank()
            || clientName.length() > MAX_CLIENT_NAME_LENGTH) {
          out.println("Invalid name. Please enter a valid name: ");
          clientName = bufferedReader.readLine();
        }

        for (String s : history) {
          out.println(s);
        }
        sendMessage(clientName + " has joined the chat.");

        String input;
        while ((input = bufferedReader.readLine()) != null) {
          if (input.isBlank()) {
            continue;
          }

          if (input.length() > MAX_MESSAGE_LENGTH) {
            out.println(
                "Message is too long. Please enter a message less than "
                    + MAX_MESSAGE_LENGTH
                    + " characters.");
            continue;
          }

          sendMessage(clientName + ": " + input);
        }
      } catch (IOException e) {
        logger.error("I/O error for client {}", clientId, e);
      } finally {
        closeClientSocket();
        if (clientName != null && !clientName.isBlank()) {
          sendMessage(clientName + " has left the chat.");
        }
      }
    }

    private void closeClientSocket() {
      try {
        if (socket != null && !socket.isClosed()) {
          socket.close();
        }
      } catch (IOException e) {
        logger.error("Error while closing socket for client {}", clientId, e);
      } finally {
        clientHandlerList.remove(this);
      }
    }

    private void sendMessage(String message) {
      if (message == null || message.isBlank() || message.length() > MAX_MESSAGE_LENGTH) {
        return;
      }

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
            logger.error("Error while broadcasting message to client {}", c.clientId, e);
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

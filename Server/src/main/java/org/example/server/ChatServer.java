package org.example.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.network.ClientConnection;
import org.example.network.TcpServer;
import org.example.repository.InMemoryMessageRepository;
import org.example.repository.MessageRepository;
import org.example.utils.ChatConfig;
import org.example.utils.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServer {
  private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

  private final ChatConfig config;
  private final MessageRepository messageRepository;
  private final Set<ClientSession> clients = ConcurrentHashMap.newKeySet();
  private final AtomicInteger clientIdGenerator = new AtomicInteger();
  private final RateLimiter connectionRateLimiter;

  private volatile boolean running;

  public ChatServer(ChatConfig config) {
    this(config, new InMemoryMessageRepository(config.getMaxHistoryLength()));
  }

  public ChatServer(ChatConfig config, MessageRepository messageRepository) {
    this.config = config;
    this.messageRepository = messageRepository;
    this.connectionRateLimiter =
        new RateLimiter(config.getRateLimitConnections(), config.getRateLimitDurationMs());
  }

  public void start() throws IOException {
    running = true;

    try (TcpServer server = new TcpServer(config.getServerHost(), config.getServerPort())) {
      System.out.println("Chat server started on port " + config.getServerPort());

      while (running) {
        try {
          Socket socket = server.accept();
          handleConnection(socket);
        } catch (IOException e) {
          if (running) {
            logger.error("Error while accepting client connection", e);
          }
        }
      }
    }
  }

  public void stop() {
    running = false;
  }

  private void handleConnection(Socket socket) {
    try {
      if (connectionRateLimiter.isLimited()) {
        new ClientConnection(socket).send("Too many connections. Please try again later.");
        socket.close();
        return;
      }

      if (clients.size() >= config.getMaxConnections()) {
        new ClientConnection(socket).send("Server is full. Please try again later.");
        socket.close();
        return;
      }

      ClientSession session =
          new ClientSession(
              config,
              clientIdGenerator.getAndIncrement(),
              new ClientConnection(socket),
              this,
              messageRepository);

      clients.add(session);
      new Thread(session).start();
    } catch (IOException e) {
      logger.error("Unable to initialize client connection", e);
      try {
        socket.close();
      } catch (IOException closeException) {
        logger.error("Unable to close rejected client socket", closeException);
      }
    }
  }

  void removeClient(ClientSession client) {
    clients.remove(client);
  }

  void broadcast(String message, ClientSession sender) {
    System.out.println(message);
    for (ClientSession client : clients) {
      if (client != sender && client.hasName()) {
        client.send(message);
      }
    }
  }
}

package org.example;

import org.example.server.ChatServer;
import org.example.utils.AppConfig;
import org.example.utils.ChatConfig;
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
    try {
      AppConfig config = new AppConfig();
      ChatConfig chatConfig = new ChatConfig(config);
      ChatServer server = new ChatServer(chatConfig);
      server.start();
    } catch (Exception e) {
      logger.error("Server failed", e);
    }
  }
}

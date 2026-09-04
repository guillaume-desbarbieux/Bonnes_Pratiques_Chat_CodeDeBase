package org.example;

import java.io.IOException;

public class Main {

  private static final int SERVER_PORT = 12345;

  /**
   * The entry point of the application. This method initializes and starts the server on a
   * predefined port, handling potential I/O exceptions that may occur during startup.
   *
   * @param args Command-line arguments passed to the program. These are currently not utilized.
   */
  public static void main(String[] args) {
    Server server = new Server(SERVER_PORT);
    try {
      server.start();
    } catch (IOException e) {
      System.out.println("error");
    }
  }
}

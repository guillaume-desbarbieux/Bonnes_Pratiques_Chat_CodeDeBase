package org.example.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.example.utils.MessageReader;

public class ClientConnection implements AutoCloseable {
  private final Socket socket;
  private final BufferedReader reader;
  private final PrintWriter writer;

  public ClientConnection(Socket socket) throws IOException {
    this.socket = socket;
    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.writer = new PrintWriter(socket.getOutputStream(), true);
  }

  public void setReadTimeout(int timeoutMs) throws IOException {
    socket.setSoTimeout(timeoutMs);
  }

  public String readLineWithLimit(int maxLength) throws IOException {
    return new MessageReader(maxLength).readLineWithLimit(reader);
  }

  public void send(String message) {
    writer.println(message);
  }

  @Override
  public void close() throws IOException {
    socket.close();
  }
}

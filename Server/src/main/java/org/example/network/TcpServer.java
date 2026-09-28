package org.example.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements AutoCloseable {
  private final ServerSocket serverSocket = new ServerSocket();

  public TcpServer(String hostname, int port) throws IOException {
    serverSocket.bind(new InetSocketAddress(hostname, port));
  }

  public Socket accept() throws IOException {
    return serverSocket.accept();
  }

  @Override
  public void close() throws IOException {
    serverSocket.close();
  }
}

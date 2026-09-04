package org.example;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  private final String serverAddress;
  private final int serverPort;
  private Socket socket;
  private ExecutorService executor;
  private BufferedReader bufferedReader;
  private int messageCount = 0;
  private final Gson gson = new Gson();

  /**
   * Constructs a new Client instance with the specified server address and port.
   *
   * @param serverAddress the address of the server to connect to
   * @param serverPort the port of the server to connect to
   */
  public Client(String serverAddress, int serverPort) {
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
  }

  /**
   * Establishes a connection to the server and manages bidirectional communication. This method
   * sets up the socket connection, initializes a thread pool, and starts two asynchronous tasks for
   * receiving and sending messages. It waits for both tasks to complete before shutting down the
   * resources.
   *
   * @throws IOException if an I/O error occurs while creating the socket or during data transfer.
   * @throws InterruptedException if the current thread is interrupted while waiting.
   * @throws ExecutionException if an exception occurs during the execution of the asynchronous
   *     tasks.
   */
  public void connect() throws IOException, InterruptedException, ExecutionException {
    socket = new Socket(serverAddress, serverPort);
    executor = Executors.newFixedThreadPool(2);

    Future<?> receiveTask = executor.submit(this::receiveMessages);
    Thread.sleep(100);
    Future<?> sendTask = executor.submit(this::sendMessages);

    receiveTask.get();
    sendTask.get();

    shutdown();
  }

  private void receiveMessages() {
    try {
      InputStream inputStream = socket.getInputStream();
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      String message;
      while ((message = bufferedReader.readLine()) != null) {
        System.out.println("\r" + message);
        System.out.print("You: ");
      }
    } catch (IOException e) {
      System.out.println("Disconnected");
    }
    // TODO: fermer le reader
  }

  private void sendMessages() {
    try {
      OutputStream outputStream = socket.getOutputStream();
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
      BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
      bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      String input;
      String author = null;
      while ((input = bufferedReader.readLine()) != null) {
        bufferedWriter.write(input);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        if (author == null) {
          author = input;
        } else {
          Message msg = new Message(author, input, new DateTime().toString());
          String json = gson.toJson(msg);
          logger.info(json);
        }
        System.out.print("You: ");
        messageCount = messageCount + 1;
      }
    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void shutdown() throws IOException {
    if (executor != null) {
      executor.shutdown();
    }
    if (socket != null && !socket.isClosed()) {
      socket.close();
    }
  }

  /**
   * Represents a message with an associated author, content, and timestamp. This class is commonly
   * used to encapsulate message data for communication purposes.
   */
  class Message {
    private final String author;
    private final String input;
    private final String timestamp;

    public Message(String author, String input, String timestamp) {
      this.author = author;
      this.input = input;
      this.timestamp = timestamp;
    }

    public String getAuthor() {
      return author;
    }

    public String getInput() {
      return input;
    }

    public String getTimestamp() {
      return timestamp;
    }
  }
}

package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private ExecutorService executor;
    private int messageCount = 0;
    private final Gson gson = new Gson();
    private static final int MAX_MESSAGE_LENGTH = 1000;
    public static final int MAX_CLIENT_NAME_LENGTH = 20;

    /**
     * Constructs a new Client instance with the specified server address and
     * port.
     *
     * @param serverAddress the address of the server to connect to
     * @param serverPort the port of the server to connect to
     */
    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * Establishes a connection to the server and manages bidirectional
     * communication. This method sets up the socket connection, initializes a
     * thread pool, and starts two asynchronous tasks for receiving and sending
     * messages. It waits for both tasks to complete before shutting down the
     * resources.
     *
     * @throws IOException if an I/O error occurs while creating the socket or
     * during data transfer.
     * @throws InterruptedException if the current thread is interrupted while
     * waiting.
     * @throws ExecutionException if an exception occurs during the execution of
     * the asynchronous tasks.
     */
    public void connect() throws IOException, InterruptedException, ExecutionException {
        try {
            socket = new Socket(serverAddress, serverPort);
            executor = Executors.newFixedThreadPool(2);

            Future<?> receiveTask = executor.submit(this::receiveMessages);
            Thread.sleep(100);
            Future<?> sendTask = executor.submit(this::sendMessages);

            receiveTask.get();
            sendTask.get();
        } finally {
            shutdown();
        }
    }

    private void receiveMessages() {
        try {
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("\r" + message);
                System.out.print("You: ");
            }
        } catch (IOException e) {
            logger.error("Error while receiving messages", e);
        }
        // TODO: fermer le reader
    }

    private void sendMessages() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String input;
            String clientName = null;

            while ((input = reader.readLine()) != null) {

                if (clientName == null && input.length() > MAX_CLIENT_NAME_LENGTH) {
                    System.out.println(
                            "Name is too long. Maximum length is "
                            + MAX_CLIENT_NAME_LENGTH
                            + " characters.");
                    continue;
                }

                if (input.length() > MAX_MESSAGE_LENGTH) {
                    System.out.println(
                            "Message is too long. Maximum length is "
                            + MAX_MESSAGE_LENGTH
                            + " characters.");
                    continue;
                }

                writer.write(input);
                writer.newLine();
                writer.flush();

                if (clientName == null) {
                    clientName = input;
                } else {
                    Message message = new Message(clientName, input, new DateTime().toString());
                    String json = gson.toJson(message);
                    logger.info(json);
                }
                System.out.print("You: ");
                messageCount = messageCount + 1;
            }
        } catch (IOException e) {
            logger.error("Error while sending message", e);
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
     * Represents a message with an associated author, content, and timestamp.
     * This class is commonly used to encapsulate message data for communication
     * purposes.
     */
    static class Message {

        private final String clientName;
        private final String input;
        private final String timestamp;

        public Message(String clientName, String input, String timestamp) {
            this.clientName = clientName;
            this.input = input;
            this.timestamp = timestamp;
        }

        public String getClientName() {
            return clientName;
        }

        public String getInput() {
            return input;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}

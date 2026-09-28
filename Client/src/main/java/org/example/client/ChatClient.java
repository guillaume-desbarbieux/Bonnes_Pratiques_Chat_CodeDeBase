package org.example.client;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import org.example.model.Message;
import org.example.network.TcpClient;
import org.example.utils.ChatConstants;
import org.example.utils.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);

    private final String serverAddress;
    private final int serverPort;
    private final InputValidator validator;
    private final Gson gson;

    public ChatClient(String serverAddress, int serverPort) {
        this(serverAddress, serverPort, new InputValidator(), new Gson());
    }

    public ChatClient(String serverAddress, int serverPort,
                      InputValidator validator, Gson gson) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.validator = validator;
        this.gson = gson;
    }

    public void connect() throws IOException, InterruptedException, ExecutionException {
        try (TcpClient connection = new TcpClient(serverAddress, serverPort)) {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            try {
                Future<?> receiveTask = executor.submit(() -> receiveMessages(connection));
                Thread.sleep(100);
                Future<?> sendTask = executor.submit(() -> sendMessages(connection));

                receiveTask.get();
                sendTask.get();
            } finally {
                executor.shutdownNow();
            }
        }
    }

    private void receiveMessages(TcpClient connection) {
        try {
            String message;
            while ((message = connection.readLine()) != null) {
                System.out.println("\r" + message);
                System.out.print("You: ");
            }
        } catch (IOException e) {
            logger.error("Error while receiving messages", e);
        }
    }

    private void sendMessages(TcpClient connection) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String input;
            String clientName = null;

            while ((input = reader.readLine()) != null) {
                if (clientName == null && !validator.isValidClientName(input)) {
                    System.out.println("Name is too long or empty. Maximum length is "
                            + ChatConstants.MAX_CLIENT_NAME_LENGTH + " characters.");
                    continue;
                }

                if (!validator.isValidMessage(input)) {
                    System.out.println("Message is too long. Maximum length is "
                            + ChatConstants.MAX_MESSAGE_LENGTH + " characters.");
                    continue;
                }

                connection.writeLine(input);

                if (clientName == null) {
                    clientName = input;
                } else {
                    Message message = new Message(clientName, input, Instant.now().toString());
                    logger.info(gson.toJson(message));
                }

                System.out.print("You: ");
            }
        } catch (IOException e) {
            logger.error("Error while sending message", e);
        }
    }
}
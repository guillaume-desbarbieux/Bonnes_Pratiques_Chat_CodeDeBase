package org.example.server;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.example.model.Message;
import org.example.model.User;
import org.example.network.ClientConnection;
import org.example.repository.MessageRepository;
import org.example.utils.ChatConstants;

public class ClientSession implements Runnable {
    private final int clientId;
    private final ClientConnection connection;
    private final ChatServer server;
    private final MessageRepository messageRepository;

    private User user;

    public ClientSession(int clientId, ClientConnection connection,
                         ChatServer server, MessageRepository messageRepository) {
        this.clientId = clientId;
        this.connection = connection;
        this.server = server;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run() {
        try {
            authenticate();
            sendHistory();
            server.broadcast(user.getName() + " has joined the chat.", this);

            readMessages();
        } catch (SocketTimeoutException e) {
            connection.send("Authentication timed out. Please try again faster !");
        } catch (IOException e) {
            // Connection errors are expected when a client disconnects abruptly.
        } finally {
            close();
        }
    }

    private void authenticate() throws IOException {
        connection.setReadTimeout(ChatConstants.AUTHENTICATION_TIMEOUT);
        connection.send("Enter your name: ");

        String name = null;

        while (name == null || name.isBlank()) {
            try {
                name = connection.readLineWithLimit(ChatConstants.MAX_CLIENT_NAME_LENGTH);
            } catch (MessageTooLongException e) {
                connection.send("Name is too long. Please enter a name less than " + ChatConstants.MAX_CLIENT_NAME_LENGTH + " characters: ");
                name = connection.readLineWithLimit(ChatConstants.MAX_CLIENT_NAME_LENGTH);
            }
        }

        connection.setReadTimeout(0);
        user = new User(clientId, name);
    }

    private void sendHistory() {
        for (Message message : messageRepository.findAll()) {
            connection.send(message.toString());
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            final String input;

            try {
                input = connection.readLineWithLimit(ChatConstants.MAX_MESSAGE_LENGTH);
            } catch (MessageTooLongException e) {
                connection.send("Message is too long. Please enter a message less than "
                        + ChatConstants.MAX_MESSAGE_LENGTH + " characters.");
                continue;
            }

            if (input == null) {
                return;
            }

            if (input.isBlank()) {
                continue;
            }

            Message message = new Message(user.getName(), input);
            messageRepository.save(message);
            server.broadcast(message.toString(), this);
        }
    }

    boolean hasName() {
        return user != null;
    }

    void send(String message) {
        connection.send(message);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException ignored) {
            // Nothing useful can be done while closing a disconnected client.
        } finally {
            server.removeClient(this);
            if (hasName()) {
                server.broadcast(user.getName() + " has left the chat.", this);
            }
        }
    }
}
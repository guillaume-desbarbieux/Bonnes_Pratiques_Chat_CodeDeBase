package org.example.model;

/**
 * Represents a message with an associated author, content, and timestamp.
 * This class is commonly used to encapsulate message data for communication
 * purposes.
 */
public class Message {
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
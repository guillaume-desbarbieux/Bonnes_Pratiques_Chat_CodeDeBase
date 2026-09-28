package org.example.utils;

public class InputValidator {

    public boolean isValidClientName(String name) {
        return name != null
                && !name.isBlank()
                && name.length() <= ChatConstants.MAX_CLIENT_NAME_LENGTH;
    }

    public boolean isValidMessage(String message) {
        return message != null
                && message.length() <= ChatConstants.MAX_MESSAGE_LENGTH;
    }
}
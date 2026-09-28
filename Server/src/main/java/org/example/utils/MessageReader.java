package org.example.utils;

import java.io.BufferedReader;
import java.io.IOException;
import org.example.server.MessageTooLongException;

public class MessageReader {
    private final int maxLength;

    public MessageReader(int maxLength) {
        this.maxLength = maxLength;
    }

    public String readLineWithLimit(BufferedReader reader)
            throws IOException {
        StringBuilder message = new StringBuilder();
        int character;

        while ((character = reader.read()) != -1) {
            if (character == '\n') {
                return message.toString();
            }
            if (character == '\r') {
                continue;
            }

            if (message.length() >= maxLength) {
                while ((character = reader.read()) != -1 && character != '\n') {
                    // Drain the rest of the oversized line.
                }
                throw new MessageTooLongException();
            }

            message.append((char) character);
        }

        return message.length() == 0 ? null : message.toString();
    }
}
package org.example.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.model.Message;

public class InMemoryMessageRepository implements MessageRepository {
    private final int maxSize;
    private final List<Message> messages = new ArrayList<>();

    public InMemoryMessageRepository(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public synchronized void save(Message message) {
        if (message == null) {
            return;
        }
        messages.add(message);
        if (messages.size() > maxSize) {
            messages.remove(0);
        }
    }

    @Override
    public synchronized List<Message> findAll() {
        return List.copyOf(messages);
    }
}
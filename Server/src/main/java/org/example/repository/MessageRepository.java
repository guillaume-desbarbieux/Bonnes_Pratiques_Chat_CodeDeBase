package org.example.repository;

import java.util.List;
import org.example.model.Message;

public interface MessageRepository {
  void save(Message message);

  List<Message> findAll();
}

package com.example.chat.messageEvent.repo;

import com.example.chat.messageEvent.entity.MessageEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageEventRepository extends JpaRepository<MessageEvent,Long> {
}

package com.example.chat.messageEvent.service;

import com.example.chat.messageEvent.entity.MessageEvent;
import com.example.chat.messageEvent.enums.EventType;
import com.example.chat.messageEvent.repo.MessageEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageEventService {

    private final MessageEventRepository repository;

    public void logEvent(Long messageId, Long userId, EventType type) {

        MessageEvent event = MessageEvent.builder()
                .messageId(messageId)
                .userId(userId)
                .eventType(type)
                .timestamp(LocalDateTime.now())
                .build();

        repository.save(event);
    }
}
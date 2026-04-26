package com.example.chat.messageEvent.entity;

import com.example.chat.messageEvent.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_event",
        indexes = {
                @Index(name = "idx_event_message", columnList = "message_id"),
                @Index(name = "idx_event_user", columnList = "user_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long messageId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String metaData; // optional JSON (old content, reason, etc.)

    private LocalDateTime timestamp;
}
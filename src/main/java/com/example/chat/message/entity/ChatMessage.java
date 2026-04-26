package com.example.chat.message.entity;

import com.example.chat.conversation.entity.Conversation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message",
        indexes = {
                @Index(name = "idx_msg_conversation", columnList = "conversation_id"),
                @Index(name = "idx_msg_created_at", columnList = "createdAt")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    private String content;

    private LocalDateTime createdAt;


}
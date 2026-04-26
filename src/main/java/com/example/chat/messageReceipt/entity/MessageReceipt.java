package com.example.chat.messageReceipt.entity;

import com.example.chat.message.entity.ChatMessage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_receipt",
        indexes = {
                @Index(name = "idx_receipt_message", columnList = "message_id"),
                @Index(name = "idx_receipt_user", columnList = "user_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ChatMessage message;

    private boolean delivered;
    private boolean seen;
    private boolean isDeletedForMe;
    private boolean isDeletedForEveryone;

    private LocalDateTime seenAt;
}
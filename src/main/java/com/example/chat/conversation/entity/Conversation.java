package com.example.chat.conversation.entity;

import com.example.chat.conversation.enums.ConversationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "conversation",
        indexes = {
                @Index(name = "idx_conversation_created_at", columnList = "createdAt")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_private_pair", columnNames = {"type", "pair_key"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConversationType type;

    @Column(name = "pair_key")
    private String pairKey;   // ONLY for PRIVATE, null for GROUP

    private LocalDateTime createdAt;
}
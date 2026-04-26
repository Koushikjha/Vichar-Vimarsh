package com.example.chat.conversationParticipant.entity;

import com.example.chat.conversation.entity.Conversation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_participant",
        indexes = {
                @Index(name = "idx_cp_conversation", columnList = "conversation_id"),
                @Index(name = "idx_cp_user", columnList = "user_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ConversationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    private LocalDateTime joinedAt;
}
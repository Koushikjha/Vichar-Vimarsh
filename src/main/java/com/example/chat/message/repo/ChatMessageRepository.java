package com.example.chat.message.repo;

import com.example.chat.message.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    ChatMessage save(ChatMessage message);
//
//    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
//
//    List<ChatMessage> findByConversationIdOrderByCreatedtDesc(Long conversationId, Pageable pageable);

    Optional<ChatMessage> findById(Long id);
//
//    List<ChatMessage> findByConversationIdOrderBySentAtAsc(Long conversationId);

    List<ChatMessage> findTop20ByConversationIdOrderByIdDesc(Long conversationId);

    List<ChatMessage> findTop20ByConversationIdAndIdLessThanOrderByIdDesc(
            Long conversationId,
            Long messageId
    );
}
package com.example.chat.conversation.repo;

import com.example.chat.conversation.entity.Conversation;
import com.example.chat.conversation.enums.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findById(Long id);
    Conversation save(Conversation conversation);
    Optional<Conversation> findByPairKey(String pairKey);
    Optional<Conversation> findByIdAndType(Long id, ConversationType type);
}
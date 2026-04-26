package com.example.chat.messageReceipt.repo;

import com.example.chat.messageReceipt.entity.MessageReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MessageReceiptRepository
        extends JpaRepository<MessageReceipt, Long> {

    MessageReceipt save(MessageReceipt receipt);

    Optional<MessageReceipt> findByMessageIdAndUserId(Long messageId, Long userId);

    @Query("""
   SELECT COUNT(r)
   FROM MessageReceipt r
   WHERE r.userId = :userId
     AND r.seen = false
     AND r.message.conversation.id = :conversationId
""")
    long countUnread(@Param("userId") Long userId,
                     @Param("conversationId") Long conversationId);
}
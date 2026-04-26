package com.example.chat.messageReceipt.service;

import com.example.chat.message.entity.ChatMessage;
import com.example.chat.messageEvent.enums.EventType;
import com.example.chat.messageEvent.service.MessageEventService;
import com.example.chat.messageReceipt.entity.MessageReceipt;
import com.example.chat.messageReceipt.repo.MessageReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageReceiptService {

    private final MessageReceiptRepository receiptRepository;
    private final MessageEventService messageEventService;

    public void createReceiptsForMessage(ChatMessage message, List<Long> participants) {

        for (Long userId : participants) {

            // sender does not need receipt
            if (userId.equals(message.getSenderId())) continue;

            receiptRepository.save(
                    MessageReceipt.builder()
                            .message(message)
                            .userId(userId)
                            .delivered(false)
                            .seen(false)
                            .isDeletedForMe(false)
                            .isDeletedForEveryone(false)
                            .build()
            );
        }
    }

    public void markDelivered(Long messageId, Long userId) {

        receiptRepository.findByMessageIdAndUserId(messageId, userId)
                .ifPresent(r -> {
                    r.setDelivered(true);
                    receiptRepository.save(r);
                    messageEventService.logEvent(messageId,userId, EventType.DELIVERED);
                });
    }

    public void markSeen(Long messageId, Long userId) {

        receiptRepository.findByMessageIdAndUserId(messageId, userId)
                .ifPresent(r -> {
                    r.setSeen(true);
                    r.setSeenAt(LocalDateTime.now());
                    receiptRepository.save(r);
                    messageEventService.logEvent(messageId,userId,EventType.SEEN);
                });
    }

    public void markDeletedForUser(Long messageId, Long userId){
        receiptRepository.findByMessageIdAndUserId(messageId, userId)
                .ifPresent(r -> {
                    r.setDeletedForMe(true);
                    receiptRepository.save(r);
                    messageEventService.logEvent(messageId,userId,EventType.DELETED_FOR_USER);
                });
    }

    public void markDeletedForEveryone(Long messageId, Long userId){
        receiptRepository.findByMessageIdAndUserId(messageId, userId)
                .ifPresent(r -> {
                    r.setDeletedForEveryone(true);
                    receiptRepository.save(r);
                    messageEventService.logEvent(messageId,userId,EventType.DELETED_FOR_EVERYONE);
                });
    }

    public long countUnreadMessages(Long userId, Long conversationId) {
        return receiptRepository.countUnread(userId, conversationId);
    }
}
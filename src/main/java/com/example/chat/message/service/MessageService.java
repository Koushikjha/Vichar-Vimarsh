package com.example.chat.message.service;

import com.example.chat.conversation.entity.Conversation;
import com.example.chat.conversation.repo.ConversationRepository;
import com.example.chat.conversation.service.ConversationService;
import com.example.chat.conversationParticipant.repo.ConversationParticipantRepository;
import com.example.chat.conversationParticipant.service.ConversationParticipantService;
import com.example.chat.message.entity.ChatMessage;
import com.example.chat.message.repo.ChatMessageRepository;
import com.example.chat.messageEvent.enums.EventType;
import com.example.chat.messageEvent.service.MessageEventService;
import com.example.chat.messageReceipt.service.MessageReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationService conversationService;
    private final ConversationParticipantService conversationParticipantService;
    private final MessageReceiptService receiptService;
    private final MessageEventService messageEventService;

    // older messages before given messageId
    public List<ChatMessage> loadOlderMessages(Long conversationId, Long lastMessageId) {

        return chatMessageRepository
                .findTop20ByConversationIdAndIdLessThanOrderByIdDesc(
                        conversationId,
                        lastMessageId
                )
                .stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }

    public ChatMessage saveMessage(Long conversationId, Long senderId, String content) {

        if (!conversationParticipantService.isUserPartOfConversation(conversationId, senderId)) {
            throw new RuntimeException("User not part of conversation");
        }

        Conversation conversation = conversationService.getById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .senderId(senderId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        ChatMessage saved = chatMessageRepository.save(message);

        // Get all participants except sender
        List<Long> participants = conversationParticipantService.findByConversationId(conversationId,senderId);

        receiptService.createReceiptsForMessage(saved, participants);

        messageEventService.logEvent( message.getId(),message.getSenderId(), EventType.SENT);

        return saved;
    }

    public Optional<ChatMessage> findById(Long messageId){
        return chatMessageRepository.findById(messageId);
    }

    // ===== EDIT MESSAGE IN DB =====
    public ChatMessage editMessage(Long messageId, String newContent) {

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setContent(newContent);

        ChatMessage chatMessage=chatMessageRepository.save(message);

        messageEventService.logEvent(messageId,message.getSenderId(),EventType.EDITED);

        return chatMessage;
    }

    public List<ChatMessage> getMessages(Long conversationId) {

        return chatMessageRepository
                .findTop20ByConversationIdOrderByIdDesc(conversationId)
                .stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }


    public Optional<ChatMessage> getById(Long messageId) {
        return chatMessageRepository.findById(messageId);
    }
}
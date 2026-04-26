package com.example.chat.service;

import com.example.chat.conversation.entity.Conversation;
import com.example.chat.conversation.service.ConversationService;
import com.example.chat.conversationParticipant.service.ConversationParticipantService;
import com.example.chat.message.entity.ChatMessage;
import com.example.chat.message.service.MessageService;
import com.example.chat.messageEvent.enums.EventType;
import com.example.chat.messageEvent.service.MessageEventService;
import com.example.chat.messageReceipt.service.MessageReceiptService;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ConversationService conversationService;
    private final ConversationParticipantService participantService;
    private final MessageService messageService;
    private final MessageReceiptService receiptService;
    private final UserService userService;

    // ===== CREATE PRIVATE CHAT =====
    public Conversation startPrivateChat(Long userA, Long userB) {
        try {
            log.info("Starting private chat between {} and {}", userA, userB);

            List<Long> common = participantService.findCommonConversationIds(userA, userB);

            if (!common.isEmpty()) {
                log.info("Existing conversation found: {}", common.get(0));
                return conversationService.getById(common.get(0)).orElseThrow();
            }

            Conversation conversation = conversationService.createPrivateConversation(userA, userB);
            participantService.addParticipantsInPrivate(conversation, userA,userB);

            log.info("New private conversation created with id {}", conversation.getId());
            return conversation;

        } catch (Exception e) {
            log.error("Error while starting private chat between {} and {}", userA, userB, e);
            throw e;
        }
    }

    public void deleteMessageForUser(Long messageId, Long userId) {
        try {
            log.info("Deleting messageId={} for userId={}", messageId, userId);

            receiptService.markDeletedForUser(messageId, userId);

            log.info("Successfully marked messageId={} as deleted for userId={}",
                    messageId, userId);

        } catch (Exception e) {
            log.error("Error while deleting messageId={} for userId={}",
                    messageId, userId, e);
            throw e;
        }
    }

    public void deleteMessageForEveryone(Long messageId, Long userId) {
        try {
            log.info("Deleting messageId={} for Everyone", messageId, userId);

            receiptService.markDeletedForEveryone(messageId, userId);

            log.info("Successfully marked messageId={} as deleted for Everyone",
                    messageId, userId);

        } catch (Exception e) {
            log.error("Error while deleting messageId={} for Everyone",
                    messageId, userId, e);
            throw e;
        }
    }

    // ===== CREATE GROUP CHAT =====
    public Conversation startGroupChat(List<Long> userIds) {
        try {
            log.info("Starting group chat for users {}", userIds);

            Conversation conversation = conversationService.createGroupConversation(userIds);
            participantService.addParticipantsInGroup(conversation, userIds);

            log.info("Group conversation created with id {}", conversation.getId());
            return conversation;

        } catch (Exception e) {
            log.error("Error while creating group chat for users {}", userIds, e);
            throw e;
        }
    }

    // ===== SEND MESSAGE =====
    public ChatMessage sendMessage(Long conversationId, Long senderId, String content) {
        try {
            log.info("User {} sending message in conversation {}", senderId, conversationId);

            if (!participantService.isUserPartOfConversation(conversationId, senderId)) {
                log.warn("Unauthorized message attempt by user {} in conversation {}", senderId, conversationId);
                throw new RuntimeException("User not part of conversation");
            }

            ChatMessage message = messageService.saveMessage(conversationId, senderId, content);

            List<Long> participants = participantService.getParticipants(conversationId);
//            receiptService.createReceiptsForMessage(message, participants);

            log.info("Message {} saved and receipts created", message.getId());
            return message;

        } catch (Exception e) {
            log.error("Error while sending message in conversation {}", conversationId, e);
            throw e;
        }
    }

    // ===== EDIT MESSAGE =====
    public ChatMessage editMessage(Long messageId, Long userId, String newContent) {
        try {
            log.info("User {} editing message {}", userId, messageId);

            ChatMessage message = messageService.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));

            if (!message.getSenderId().equals(userId)) {
                throw new RuntimeException("You can edit only your own messages");
            }

            Long conversationId = message.getConversation().getId();
            if (!participantService.isUserPartOfConversation(conversationId, userId)) {
                throw new RuntimeException("User not part of conversation");
            }

            ChatMessage updated = messageService.editMessage(messageId, newContent);

            log.info("Message {} edited successfully", messageId);

            return updated;

        } catch (Exception e) {
            log.error("Error while editing message {}", messageId, e);
            throw e;
        }
    }

    // ===== GET CHAT HISTORY =====
    public List<ChatMessage> getChatHistory(Long conversationId, String phone) {

        try {
            log.info("User {} fetching latest messages for conversation {}", phone, conversationId);

            User user = userService.getByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!participantService.isUserPartOfConversation(conversationId, user.getId())) {
                log.warn("Unauthorized access by user {} to conversation {}", phone, conversationId);
                throw new RuntimeException("Access denied");
            }

            List<ChatMessage> messages = messageService.getMessages(conversationId);

            log.info("Fetched {} messages for conversation {}", messages.size(), conversationId);

            return messages;

        } catch (Exception e) {
            log.error("Error fetching messages for conversation {}", conversationId, e);
            throw e;
        }
    }

    // ===== LOAD OLDER MESSAGES =====
    public List<ChatMessage> loadOlderMessages(Long conversationId,
                                               String phone,
                                               Long lastMessageId) {

        try {
            log.info("User {} loading older messages before messageId {} in conversation {}",
                    phone, lastMessageId, conversationId);

            User user = userService.getByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!participantService.isUserPartOfConversation(conversationId, user.getId())) {
                log.warn("Unauthorized access by user {} to conversation {}", phone, conversationId);
                throw new RuntimeException("Access denied");
            }

            List<ChatMessage> messages =
                    messageService.loadOlderMessages(conversationId, lastMessageId);

            log.info("Loaded {} older messages for conversation {}", messages.size(), conversationId);

            return messages;

        } catch (Exception e) {
            log.error("Error loading older messages for conversation {}", conversationId, e);
            throw e;
        }
    }

    // ===== MARK DELIVERED =====
    public void markDelivered(Long messageId, Long userId) {
        try {
            log.info("Marking message {} as delivered for user {}", messageId, userId);
            receiptService.markDelivered(messageId, userId);
        } catch (Exception e) {
            log.error("Error marking delivered for message {}", messageId, e);
            throw e;
        }
    }

    // ===== MARK SEEN =====
    public void markSeen(Long messageId, Long userId) {
        try {
            log.info("Marking message {} as seen for user {}", messageId, userId);
            receiptService.markSeen(messageId, userId);
        } catch (Exception e) {
            log.error("Error marking seen for message {}", messageId, e);
            throw e;
        }
    }

    // ===== UNREAD COUNT =====
    public long getUnreadCount(Long userId, Long conversationId) {
        try {
            log.info("Fetching unread count for user {} in conversation {}", userId, conversationId);
            return receiptService.countUnreadMessages(userId, conversationId);
        } catch (Exception e) {
            log.error("Error fetching unread count for conversation {}", conversationId, e);
            throw e;
        }
    }
}
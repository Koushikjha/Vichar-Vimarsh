package com.example.chat.controller;

import com.example.chat.message.entity.ChatMessage;
import com.example.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 1️⃣ Start or get private conversation
    @PostMapping("/private/{otherUserId}")
    public Long startPrivateChat(@PathVariable Long otherUserId,
                                 @RequestParam Long myUserId) {
        return chatService.startPrivateChat(myUserId, otherUserId).getId();
    }

    // 2️⃣ Create group conversation
    @PostMapping("/group")
    public Long createGroupChat(@RequestBody List<Long> userIds) {
        return chatService.startGroupChat(userIds).getId();
    }

    // 3️⃣ Send message
    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestParam Long conversationId,
                                   @RequestParam Long senderId,
                                   @RequestParam String content) {
        return chatService.sendMessage(conversationId, senderId, content);
    }

    // 4️⃣ Get messages of a conversation
    @GetMapping("/messages/{conversationId}")
    public List<ChatMessage> getMessages(@PathVariable Long conversationId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        String phone = userDetails.getUsername();
        return chatService.getChatHistory(conversationId,phone);
    }

    @PostMapping("/messages/edit")
    public ChatMessage editMessage(@RequestParam Long messageId,
                                   @RequestParam Long userId,
                                   @RequestParam String newContent) {

        return chatService.editMessage(messageId, userId, newContent);
    }

    @PostMapping("/message/delete")
    public void deleteMessageForUser(
            @RequestParam Long messageId,
            @RequestParam Long userId
    ) {
        chatService.deleteMessageForUser(messageId, userId);
    }

    @PostMapping("/message/delete/everyone")
    public void deleteMessageForEveryone(
            @RequestParam Long messageId,
            @RequestParam Long userId
    ) {
        chatService.deleteMessageForEveryone(messageId, userId);
    }

    // 5️⃣ Mark delivered
    @PostMapping("/delivered")
    public void markDelivered(@RequestParam Long messageId,
                              @RequestParam Long userId) {
        chatService.markDelivered(messageId, userId);
    }

    // 6️⃣ Mark seen
    @PostMapping("/seen")
    public void markSeen(@RequestParam Long messageId,
                         @RequestParam Long userId) {
        chatService.markSeen(messageId, userId);
    }

    // 7️⃣ Unread count
    @GetMapping("/unread")
    public long unreadCount(@RequestParam Long conversationId,
                            @RequestParam Long userId) {
        return chatService.getUnreadCount(userId, conversationId);
    }

    @GetMapping("/messages/older/{conversationId}")
    public List<ChatMessage> loadOlderMessages(
            @PathVariable Long conversationId,
            @RequestParam Long lastMessageId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String phone = userDetails.getUsername();

        return chatService.loadOlderMessages(conversationId, phone, lastMessageId);
    }
}
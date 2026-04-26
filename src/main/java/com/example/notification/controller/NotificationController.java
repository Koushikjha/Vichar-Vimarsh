package com.example.notification.controller;

import com.example.auth.util.JwtUtil;
import com.example.chat.message.entity.ChatMessage;
import com.example.chat.service.ChatService;
import com.example.notification.dto.MessageDto;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;



    // =========================
    // SEND MESSAGES
    // =========================
    @MessageMapping("/privateMessage")
    public void privateChat(MessageDto messageDTO,
                            SimpMessageHeaderAccessor headers) {

        String Phone = extractPhoneFromCookie(headers);

        Optional<User> user=userRepository.findByPhone(Phone);

        ChatMessage savedMessage =
                chatService.sendMessage(
                        messageDTO.getConversationId(),
                        user.get().getId(),
                        messageDTO.getContent()
                );

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + savedMessage.getConversation().getId(),
                savedMessage
        );
    }

    // =========================
    // COOKIE → USERNAME
    // =========================
    private String extractPhoneFromCookie(SimpMessageHeaderAccessor headers) {

        if (headers.getSessionAttributes() == null) {
            return "anonymous";
        }

        Object cookieObj = headers.getSessionAttributes().get("cookie");

        if (cookieObj == null) {
            return "anonymous";
        }

        String cookieHeader = cookieObj.toString();

        for (String cookie : cookieHeader.split(";")) {
            cookie = cookie.trim();

            if (cookie.startsWith("JWT_TOKEN=")) {
                String token = cookie.substring("JWT_TOKEN=".length());
                return jwtUtil.extractPhone(token);
            }
        }

        return "anonymous";
    }
}
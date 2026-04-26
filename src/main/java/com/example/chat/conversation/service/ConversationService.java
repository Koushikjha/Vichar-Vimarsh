package com.example.chat.conversation.service;

import com.example.chat.conversation.entity.Conversation;
import com.example.chat.conversationParticipant.entity.ConversationParticipant;
import com.example.chat.conversation.enums.ConversationType;
import com.example.chat.conversationParticipant.repo.ConversationParticipantRepository;
import com.example.chat.conversation.repo.ConversationRepository;
import com.example.chat.conversationParticipant.service.ConversationParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantService conversationParticipantService;



    public Conversation createPrivateConversation(Long userA, Long userB) {

        String pairKey = Math.min(userA, userB) + "_" + Math.max(userA, userB);

        return conversationRepository.findByPairKey(pairKey)
                .orElseGet(() -> {

                    Conversation conversation = Conversation.builder()
                            .type(ConversationType.PRIVATE)
                            .pairKey(pairKey)
                            .createdAt(LocalDateTime.now())
                            .build();

                    Conversation saved = conversationRepository.save(conversation);

                    conversationParticipantService
                            .addParticipantsInPrivate(saved, userA, userB);

                    return saved;
                });

//        participantRepository.save(
//                ConversationParticipant.builder()
//                        .conversation(conversation)
//                        .userId(userA)
//                        .joinedAt(LocalDateTime.now())
//                        .build()
//        );
//
//        participantRepository.save(
//                ConversationParticipant.builder()
//                        .conversation(conversation)
//                        .userId(userB)
//                        .joinedAt(LocalDateTime.now())
//                        .build()
//        );

    }

    public Conversation createGroupConversation(List<Long> userIds) {

        Conversation conversation = Conversation.builder()
                .type(ConversationType.GROUP)
                .pairKey(null)
                .createdAt(LocalDateTime.now())
                .build();

        conversation = conversationRepository.save(conversation);

        conversationParticipantService.addParticipantsInGroup(conversation,userIds);

//        for (Long userId : userIds) {
//            participantRepository.save(
//                    ConversationParticipant.builder()
//                            .conversation(conversation)
//                            .userId(userId)
//                            .joinedAt(LocalDateTime.now())
//                            .build()
//            );
//        }

        return conversation;
    }

    public Optional<Conversation> getById(Long conversationId) {
        return conversationRepository.findById(conversationId);
    }

    public List<Long> getConversationIdsOfUser(Long userId) {
        return conversationParticipantService.getConversationIdsOfUser(userId);
    }
}
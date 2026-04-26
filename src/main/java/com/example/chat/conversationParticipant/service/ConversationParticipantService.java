package com.example.chat.conversationParticipant.service;

import com.example.chat.conversation.entity.Conversation;
import com.example.chat.conversation.enums.ConversationType;
import com.example.chat.conversation.service.ConversationService;
import com.example.chat.conversationParticipant.repo.ConversationParticipantRepository;
import com.example.chat.conversation.repo.ConversationRepository;
import com.example.chat.conversationParticipant.entity.ConversationParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationParticipantService {

    private final ConversationParticipantRepository participantRepository;
    private final ConversationRepository conversationRepository;

    public void addParticipantsInGroup(Conversation conversation, List<Long> userIds) {

//        Conversation conversation = conversationRepository.findById(conversationId)
//                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        for (Long userId : userIds) {

            boolean exists = participantRepository
                    .existsByConversationIdAndUserId(conversation.getId(), userId);

            if (!exists) {
                participantRepository.save(
                        ConversationParticipant.builder()
                                .conversation(conversation)
                                .userId(userId)
                                .joinedAt(LocalDateTime.now())
                                .build()
                );
            }
        }
    }

    public void addParticipantsInPrivate(Conversation conversation, Long userA,Long userB) {

//        Conversation conversation = conversationRepository.findById(conversation)
//                .orElseThrow(() -> new RuntimeException("Conversation not found"));



        boolean exists1 = participantRepository
                .existsByConversationIdAndUserId(conversation.getId(), userA);

        if (!exists1) {
            participantRepository.save(
                    ConversationParticipant.builder()
                            .conversation(conversation)
                            .userId(userA)
                            .joinedAt(LocalDateTime.now())
                            .build()
            );
        }

        boolean exists2 = participantRepository
                .existsByConversationIdAndUserId(conversation.getId(), userB);

        if (!exists2) {
            participantRepository.save(
                    ConversationParticipant.builder()
                            .conversation(conversation)
                            .userId(userB)
                            .joinedAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    public List<Long> findCommonConversationIds(Long userA, Long userB) {
        return participantRepository.findCommonConversationIds(userA, userB);
    }

    public void removeParticipant(Long conversationId, Long userId) {
        participantRepository
                .deleteByConversationIdAndUserId(conversationId, userId);
    }


    //include sender
    public List<Long> getParticipants(Long conversationId) {
        return participantRepository.findByConversationId(conversationId)
                .stream()
                .map(ConversationParticipant::getUserId)
                .toList();
    }

    //exclude sender
    public List<Long> findByConversationId(Long conversationId,Long senderId){
        return participantRepository.findByConversationId(conversationId)
                .stream()
                .map(cp -> cp.getUserId())
                .filter(id -> !id.equals(senderId))
                .toList();
    }





    public boolean isUserPartOfConversation(Long conversationId, Long userId) {
        return participantRepository
                .existsByConversationIdAndUserId(conversationId, userId);
    }

    public List<Long> getConversationIdsOfUser(Long userId){
        return participantRepository.findByUserId(userId)
                .stream()
                .map(cp -> cp.getConversation().getId())
                .toList();
    }
}
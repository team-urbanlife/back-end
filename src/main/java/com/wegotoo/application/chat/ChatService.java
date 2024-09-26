package com.wegotoo.application.chat;

import static com.wegotoo.exception.ErrorCode.NOT_VALID_USER;

import com.wegotoo.application.chat.request.ChatSendServiceRequest;
import com.wegotoo.application.event.ChatMessageSentEvent;
import com.wegotoo.domain.chat.Chat;
import com.wegotoo.domain.chat.repository.ChatRepository;
import com.wegotoo.domain.user.User;
import com.wegotoo.domain.user.repository.UserRepository;
import com.wegotoo.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ChatResponse sendChatMessage(Long userId, ChatSendServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_VALID_USER));

        Chat chat = chatRepository.save(request.toDocument(user.getId()));

        eventPublisher.publishEvent(ChatMessageSentEvent.to(user.getId(), request.getMessage()));

        return ChatResponse.of(user, chat);
    }

}

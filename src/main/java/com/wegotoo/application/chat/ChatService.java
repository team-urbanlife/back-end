package com.wegotoo.application.chat;

import static com.wegotoo.exception.ErrorCode.CHAT_ROOM_NOT_FOUND;
import static com.wegotoo.exception.ErrorCode.NOT_VALID_USER;
import static com.wegotoo.exception.ErrorCode.USER_NOT_FOUND;

import com.wegotoo.application.CursorResponse;
import com.wegotoo.application.chat.request.ChatSendServiceRequest;
import com.wegotoo.application.chat.response.ChatResponse;
import com.wegotoo.application.chat.response.LastReadResponse;
import com.wegotoo.domain.chat.Chat;
import com.wegotoo.domain.chat.ChatRoomStatus;
import com.wegotoo.domain.chat.repository.ChatRepository;
import com.wegotoo.domain.chat.repository.ChatRoomStatusRepository;
import com.wegotoo.domain.user.User;
import com.wegotoo.domain.user.Users;
import com.wegotoo.domain.user.repository.UserRepository;
import com.wegotoo.exception.BusinessException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomStatusRepository chatRoomStatusRepository;

    public CursorResponse<String, ChatResponse> findAllChats(Long userId, Long chatRoomId, String cursorId,
                                                             Integer limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        List<Chat> chats = chatRepository.findAllByChatRoomId(chatRoomId, cursorId, limit);

        Set<Long> userIds = chats.stream().map(Chat::getSenderId).collect(Collectors.toSet());
        Users users = Users.of(userRepository.findAllById(userIds));

        return CursorResponse.of(ChatResponse.toList(users, chats), limit);
    }

    public LastReadResponse findLastReadMessages(Long userId, Long chatRoomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        ChatRoomStatus chatRoomStatus = chatRoomStatusRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(CHAT_ROOM_NOT_FOUND));

        return LastReadResponse.of(chatRoomStatus);
    }

    @Transactional
    public ChatResponse sendChatMessage(Long userId, ChatSendServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(NOT_VALID_USER));

        Chat chat = chatRepository.save(request.toDocument(user.getId()));
        return ChatResponse.of(user, chat);
    }

}

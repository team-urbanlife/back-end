package com.wegotoo.application.notification;

import com.wegotoo.application.notification.event.NotificationEvent;
import com.wegotoo.domain.notification.repository.NotificationRepository;
import com.wegotoo.domain.user.User;
import com.wegotoo.domain.user.repository.UserRepository;
import com.wegotoo.exception.BusinessException;
import com.wegotoo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(NotificationEvent event) {
        User from = userRepository.findById(event.getFromId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User to = userRepository.findById(event.getToId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        notificationRepository.save(event.toEntity(from, to));
    }

}
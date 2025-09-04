package com.wegotoo.application.comment;

import com.wegotoo.application.comment.request.CommentWriteServiceRequest;
import com.wegotoo.application.notification.event.NotificationEvent;
import com.wegotoo.domain.comment.repository.CommentRepository;
import com.wegotoo.domain.post.Post;
import com.wegotoo.domain.post.repository.PostRepository;
import com.wegotoo.domain.user.User;
import com.wegotoo.domain.user.repository.UserRepository;
import com.wegotoo.exception.BusinessException;
import com.wegotoo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void writeComment(CommentWriteServiceRequest request) {
        User fromUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User toUser = userRepository.findById(post.getUser().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        commentRepository.save(request.toEntity(fromUser, post));
        eventPublisher.publishEvent(NotificationEvent.of(fromUser, toUser, "COMMENT"));
    }

}

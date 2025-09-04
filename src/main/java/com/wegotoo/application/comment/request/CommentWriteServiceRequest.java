package com.wegotoo.application.comment.request;

import com.wegotoo.domain.comment.Comment;
import com.wegotoo.domain.post.Post;
import com.wegotoo.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentWriteServiceRequest {

    private Long userId;
    private Long postId;
    private String content;

    @Builder
    private CommentWriteServiceRequest(Long userId, Long postId, String content) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
    }

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .build();
    }

}

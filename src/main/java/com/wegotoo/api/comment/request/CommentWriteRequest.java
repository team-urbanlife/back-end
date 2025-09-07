package com.wegotoo.api.comment.request;

import com.wegotoo.application.comment.request.CommentWriteServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentWriteRequest {

    private String content;

    @Builder
    private CommentWriteRequest(String content) {
        this.content = content;
    }

    public CommentWriteServiceRequest toService(Long userId, Long postId) {
        return CommentWriteServiceRequest.builder()
                .userId(userId)
                .postId(postId)
                .content(content)
                .build();
    }

}

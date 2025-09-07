package com.wegotoo.api.comment;

import com.wegotoo.api.ApiResponse;
import com.wegotoo.api.comment.request.CommentWriteRequest;
import com.wegotoo.application.comment.CommentService;
import com.wegotoo.infra.resolver.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/v1/posts/{postId}/comments")
    public ApiResponse<Void> writeComment(@Auth Long id, @PathVariable Long postId, @RequestBody CommentWriteRequest request) {
        commentService.writeComment(request.toService(id, postId));
        return ApiResponse.ok();
    }

}

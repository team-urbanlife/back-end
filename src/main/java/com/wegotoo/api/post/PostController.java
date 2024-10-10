package com.wegotoo.api.post;

import com.wegotoo.api.ApiResponse;
import com.wegotoo.api.post.request.PostEditRequest;
import com.wegotoo.api.post.request.PostWriteRequest;
import com.wegotoo.application.OffsetLimit;
import com.wegotoo.application.SliceResponse;
import com.wegotoo.application.post.PostService;
import com.wegotoo.application.post.response.PostFindAllResponse;
import com.wegotoo.application.post.response.PostFindOneResponse;
import com.wegotoo.infra.resolver.auth.Auth;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping("/v1/posts")
    public ApiResponse<PostFindOneResponse> writePost(@Auth Long userId,
                                                      @RequestBody @Valid PostWriteRequest request) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        return ApiResponse.ok(postService.writePost(userId, request.toService(), now));
    }

    @GetMapping("/v1/posts")
    public ApiResponse<SliceResponse<PostFindAllResponse>> findAllPosts(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "4") Integer size
    ) {
        return ApiResponse.ok(postService.findAllPost(OffsetLimit.of(page, size)));
    }

    @GetMapping("/v1/users/likes/posts")
    public ApiResponse<SliceResponse<PostFindAllResponse>> findAllLikePosts(
            @Auth Long userId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "4") Integer size
    ) {
        return ApiResponse.ok(postService.findAllLikePost(userId, OffsetLimit.of(page, size)));
    }

    @GetMapping("/v1/posts/{postId}")
    public ApiResponse<PostFindOneResponse> findOnePost(@PathVariable("postId") Long postId) {
        return ApiResponse.ok(postService.findOnePost(postId));
    }

    @PatchMapping("/v1/posts/{postId}")
    public ApiResponse<Void> editPost(@Auth Long userId,
                                      @PathVariable("postId") Long postId,
                                      @RequestBody PostEditRequest request) {
        postService.editPost(userId, postId, request.toService());
        return ApiResponse.ok();
    }

    @DeleteMapping("/v1/posts/{postId}")
    public ApiResponse<Void> deletePost(@Auth Long userId,
                                        @PathVariable("postId") Long postId) {
        postService.deletePost(userId, postId);
        return ApiResponse.ok();
    }
}

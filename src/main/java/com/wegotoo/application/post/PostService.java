package com.wegotoo.application.post;

import static com.wegotoo.exception.ErrorCode.CONTENT_NOT_FOUND;
import static com.wegotoo.exception.ErrorCode.POST_NOT_FOUND;
import static com.wegotoo.exception.ErrorCode.UNAUTHORIZED_REQUEST;
import static com.wegotoo.exception.ErrorCode.USER_NOT_FOUND;

import com.wegotoo.application.OffsetLimit;
import com.wegotoo.application.SliceResponse;
import com.wegotoo.application.post.request.ContentEditServiceRequest;
import com.wegotoo.application.post.request.PostEditServiceRequest;
import com.wegotoo.application.post.request.PostWriteServiceRequest;
import com.wegotoo.application.post.response.ContentResponse;
import com.wegotoo.application.post.response.PostFindAllResponse;
import com.wegotoo.application.post.response.PostFindOneResponse;
import com.wegotoo.domain.post.Content;
import com.wegotoo.domain.post.Post;
import com.wegotoo.domain.post.repository.ContentRepository;
import com.wegotoo.domain.post.repository.PostRepository;
import com.wegotoo.domain.post.repository.response.ContentImageQueryEntity;
import com.wegotoo.domain.post.repository.response.ContentTextQueryEntity;
import com.wegotoo.domain.post.repository.response.PostQueryEntity;
import com.wegotoo.domain.user.User;
import com.wegotoo.domain.user.repository.UserRepository;
import com.wegotoo.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostFindOneResponse writePost(Long userId, PostWriteServiceRequest request, LocalDateTime date) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Post post = Post.create(request.getTitle(), user, date);
        Post postSave = postRepository.save(post);

        List<Content> contents = request.getContents().stream()
                .map(c -> Content.create(c.getType(), c.getText(), postSave))
                .toList();
        List<Content> contentsSave = contentRepository.saveAll(contents);

        return PostFindOneResponse.of(postSave, user, ContentResponse.toList(contentsSave), 0L);
    }

    public SliceResponse<PostFindAllResponse> findAllPost(OffsetLimit offsetLimit) {
        List<PostQueryEntity> posts = postRepository.findAllPost(offsetLimit.getOffset(), offsetLimit.getLimit());

        List<Long> postIds = posts.stream().map(PostQueryEntity::getId).toList();

        List<ContentTextQueryEntity> allContentTexts = contentRepository.findAllPostWithContentText(postIds);
        List<ContentImageQueryEntity> allContentImages = contentRepository.findAllPostWithContentImage(postIds);

        List<ContentTextQueryEntity> firstContentText = getFirstContentText(allContentTexts);
        List<ContentImageQueryEntity> firstContentImage = getFirstContentImage(allContentImages);

        return SliceResponse.of(PostFindAllResponse.toList(posts, firstContentText, firstContentImage),
                offsetLimit.getOffset(), offsetLimit.getLimit());
    }

    public SliceResponse<PostFindAllResponse> findAllLikePost(Long userId, OffsetLimit offsetLimit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        List<PostQueryEntity> posts = postRepository.findAllLikePost(user.getId(), offsetLimit.getOffset(),
                offsetLimit.getLimit());

        List<Long> postIds = posts.stream().map(PostQueryEntity::getId).toList();

        List<ContentTextQueryEntity> allContentTexts = contentRepository.findAllPostWithContentText(postIds);
        List<ContentImageQueryEntity> allContentImages = contentRepository.findAllPostWithContentImage(postIds);

        List<ContentTextQueryEntity> firstContentText = getFirstContentText(allContentTexts);
        List<ContentImageQueryEntity> firstContentImage = getFirstContentImage(allContentImages);

        return SliceResponse.of(PostFindAllResponse.toList(posts, firstContentText, firstContentImage),
                offsetLimit.getOffset(), offsetLimit.getLimit());
    }

    public PostFindOneResponse findOnePost(Long postId) {
        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new BusinessException(POST_NOT_FOUND));

        Long likeCount = postRepository.findPostLikeCount(post.getId());
        List<Content> contents = contentRepository.findAllByPostIdOrderByIdAsc(post.getId());
        return PostFindOneResponse.of(post, post.getUser(), ContentResponse.toList(contents), likeCount);
    }

    @Transactional
    public void editPost(Long userId, Long postId, PostEditServiceRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new BusinessException(POST_NOT_FOUND));

        if (!post.isOwner(user.getId())) {
            throw new BusinessException(UNAUTHORIZED_REQUEST);
        }

        post.edit(request.getTitle());

        List<Long> contentIds = request.getContents().stream()
                .map(ContentEditServiceRequest::getId).collect(Collectors.toList());

        List<Content> contents = contentRepository.findByIdIn(contentIds);

        Map<Long, ContentEditServiceRequest> requestMap = request.getContents().stream()
                .collect(Collectors.toMap(ContentEditServiceRequest::getId, r -> r));

        contents.forEach(content -> {
            ContentEditServiceRequest match = requestMap.get(content.getId());

            if (match == null) {
                throw new BusinessException(CONTENT_NOT_FOUND);
            }

            content.edit(match.getType(), match.getText());
        });
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        Post post = postRepository.findByIdWithUser(postId).orElseThrow(() -> new BusinessException(POST_NOT_FOUND));

        if (!post.isOwner(user.getId())) {
            throw new BusinessException(UNAUTHORIZED_REQUEST);
        }

        contentRepository.deleteAllByPostId(post);
        postRepository.delete(post);
    }

    private List<ContentImageQueryEntity> getFirstContentImage(List<ContentImageQueryEntity> allContentImages) {
        return allContentImages.stream()
                .collect(Collectors.groupingBy(ContentImageQueryEntity::getPostId))
                .values().stream()
                .map(list -> list.stream()
                        .min(Comparator.comparing(ContentImageQueryEntity::getContentId))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<ContentTextQueryEntity> getFirstContentText(List<ContentTextQueryEntity> allContentTexts) {
        return allContentTexts.stream()
                .collect(Collectors.groupingBy(ContentTextQueryEntity::getPostId))
                .values().stream()
                .map(list -> list.stream()
                        .min(Comparator.comparing(ContentTextQueryEntity::getContentId))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

}


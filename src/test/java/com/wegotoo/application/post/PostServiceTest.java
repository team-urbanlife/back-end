package com.wegotoo.application.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.*;

import com.wegotoo.application.OffsetLimit;
import com.wegotoo.application.ServiceTestSupport;
import com.wegotoo.application.SliceResponse;
import com.wegotoo.application.post.request.ContentEditServiceRequest;
import com.wegotoo.application.post.request.ContentWriteServiceRequest;
import com.wegotoo.application.post.request.PostEditServiceRequest;
import com.wegotoo.application.post.request.PostWriteServiceRequest;
import com.wegotoo.application.post.response.PostFindAllResponse;
import com.wegotoo.application.post.response.PostFindOneResponse;
import com.wegotoo.domain.like.PostLike;
import com.wegotoo.domain.like.repository.PostLikeRepository;
import com.wegotoo.domain.post.Content;
import com.wegotoo.domain.post.ContentType;
import com.wegotoo.domain.post.Post;
import com.wegotoo.domain.post.repository.ContentRepository;
import com.wegotoo.domain.post.repository.PostRepository;
import com.wegotoo.domain.user.User;
import com.wegotoo.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostServiceTest extends ServiceTestSupport {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        contentRepository.deleteAllInBatch();
        postLikeRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("유저가 게시글을 작성할 수 있다.")
    void writePost() throws Exception {
        // given
        User user = getUser();
        userRepository.save(user);

        List<ContentWriteServiceRequest> contents = IntStream.range(0, 4)
                .mapToObj(i -> ContentWriteServiceRequest.builder()
                        .type(ContentType.T)
                        .text("내용 " + i)
                        .build()).toList();
        PostWriteServiceRequest request = PostWriteServiceRequest.builder()
                .title("제목")
                .contents(contents)
                .build();
        // when
        PostFindOneResponse response = postService.writePost(user.getId(), request, LocalDateTime.now());

        // then
        assertThat(response)
                .extracting("id", "title")
                .contains(response.getId(), "제목");
        assertThat(response.getContents())
                .extracting("type", "text")
                .containsExactly(
                        tuple(ContentType.T, "내용 0"),
                        tuple(ContentType.T, "내용 1"),
                        tuple(ContentType.T, "내용 2"),
                        tuple(ContentType.T, "내용 3")
                );
    }

    @Test
    @DisplayName("유저가 게시글을 수정할 수 있다.")
    void editPost() throws Exception {
        // given
        User user = getUser();
        userRepository.save(user);

        Post post = getPost(user);
        postRepository.save(post);

        List<Content> contents = getContentList(post);
        contentRepository.saveAll(contents);

        ContentEditServiceRequest content1 = getContentEdit(contents.get(0).getId(), "첫 문단 수정");
        ContentEditServiceRequest content2 = getContentEdit(contents.get(1).getId(), "두 번째 문단 수정");

        List<ContentEditServiceRequest> contentRequests = List.of(content1, content2);

        PostEditServiceRequest request = PostEditServiceRequest.builder()
                .title("제목 수정")
                .contents(contentRequests)
                .build();

        // when
        postService.editPost(user.getId(), post.getId(), request);

        // then
        List<Post> response = postRepository.findAll();
        List<Content> contentList = contentRepository.findAll();
        assertThat(response.get(0))
                .extracting("id", "title")
                .contains(response.get(0).getId(), "제목 수정");

        assertThat(contentList.get(0))
                .extracting("id", "text")
                .contains(contentList.get(0).getId(), "첫 문단 수정");

    }

    @Test
    @DisplayName("유저가 게시글을 전체 조회 했을 때 객체는 게시글 번호, 제목, 첫 문단, 썸네일, 유저 이름, 유저 이미지가 담긴다.")
    void findAllPosts() throws Exception {
        //given
        User user = User.builder()
                .name("user")
                .email("user@email.com")
                .profileImage("이미지")
                .build();
        userRepository.save(user);

        Post post = Post.builder()
                .title("제목")
                .view(0)
                .user(user)
                .build();
        postRepository.save(post);

        Post post1 = Post.builder()
                .title("제목")
                .view(0)
                .user(user)
                .build();
        postRepository.save(post1);

        Content content1 = getContent(post, ContentType.T, "글1");
        Content content2 = getContent(post, ContentType.IMAGE, "이미지1");
        Content content3 = getContent(post, ContentType.T, "글2");
        Content content4 = getContent(post, ContentType.IMAGE, "이미지2");

        Content content5 = getContent(post1, ContentType.IMAGE, "이미지");
        Content content6 = getContent(post1, ContentType.IMAGE, "이미지1");
        Content content7 = getContent(post1, ContentType.IMAGE, "이미지");
        Content content8 = getContent(post1, ContentType.T, "글123");

        contentRepository.saveAll(
                List.of(content1, content2, content3, content4, content5, content6, content7, content8));
        OffsetLimit offsetLimit = OffsetLimit.builder()
                .offset(0)
                .limit(10)
                .build();

        // when
        SliceResponse<PostFindAllResponse> response = postService.findAllPost(offsetLimit);

        // then
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getContent().get(0))
                .extracting("postId", "title", "content", "thumbnail", "userName", "userProfileImage")
                .contains(post.getId(), "제목", "글1", "이미지1", user.getName(), "이미지");
    }

    @Test
    @DisplayName("좋아요한 게시글 조회")
    void findAllPostLike() {
        // given
        User userA = userRepository.save(createUser("userA"));
        User userB = userRepository.save(createUser("userB"));

        Post postA = Post.builder()
                .title("제목")
                .view(0)
                .user(userA)
                .build();
        postRepository.save(postA);

        Post postB = Post.builder()
                .title("제목")
                .view(0)
                .user(userB)
                .build();
        postRepository.save(postB);

        PostLike postLike = PostLike.builder()
                .post(postB)
                .user(userA)
                .build();
        postLikeRepository.save(postLike);

        Content content1 = getContent(postB, ContentType.IMAGE, "이미지");
        Content content2 = getContent(postB, ContentType.IMAGE, "이미지1");
        Content content3 = getContent(postB, ContentType.IMAGE, "이미지");
        Content content4 = getContent(postB, ContentType.T, "글123");

        contentRepository.saveAll(List.of(content1, content2, content3, content4));

        // when
        SliceResponse<PostFindAllResponse> response = postService.findAllLikePost(userA.getId(),
                OffsetLimit.of(1, 5));

        // then
        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("게시글을 단건 조회 하면 컨텐츠 블록도 같이 출력된다.")
    void findOnePost() throws Exception {
        // given
        User user = User.builder()
                .name("user")
                .email("user@email.com")
                .build();
        userRepository.save(user);

        Post post = Post.builder()
                .title("제목 ")
                .view(0)
                .user(user)
                .build();
        postRepository.save(post);

        Content content1 = getContent(post, ContentType.T, "글1");
        Content content2 = getContent(post, ContentType.IMAGE, "이미지1");
        Content content3 = getContent(post, ContentType.T, "글2");
        Content content4 = getContent(post, ContentType.IMAGE, "이미지2");

        contentRepository.saveAll(List.of(content1, content2, content3, content4));
        // when
        PostFindOneResponse response = postService.findOnePost(post.getId());
        // then
        assertThat(response.getContents().size()).isEqualTo(4);
    }

    private static User createUser(String username) {
        return User.builder()
                .name(username)
                .email(username + "@gmail.com")
                .build();
    }

    private static Content getContent(Post post, ContentType type, String text) {
        return Content.builder()
                .type(type)
                .post(post)
                .text(text)
                .build();
    }

    private static List<Content> getContentList(Post post) {
        return IntStream.range(0, 2)
                .mapToObj(i -> Content.builder()
                        .type(ContentType.T)
                        .text("내용 " + i)
                        .post(post)
                        .build()).toList();
    }

    private static Post getPost(User user) {
        return Post.builder()
                .title("제목")
                .view(0)
                .user(user)
                .build();
    }

    private static User getUser() {
        return User.builder()
                .name("user")
                .email("user@email.com")
                .build();
    }

    private static ContentEditServiceRequest getContentEdit(Long id, String text) {
        return ContentEditServiceRequest.builder()
                .id(id)
                .type(ContentType.T)
                .text(text)
                .build();
    }
}
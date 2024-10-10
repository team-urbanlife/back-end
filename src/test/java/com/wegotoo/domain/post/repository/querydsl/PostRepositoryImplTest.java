package com.wegotoo.domain.post.repository.querydsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.wegotoo.config.QueryDslConfig;
import com.wegotoo.domain.like.PostLike;
import com.wegotoo.domain.like.repository.PostLikeRepository;
import com.wegotoo.domain.post.Post;
import com.wegotoo.domain.post.repository.PostRepository;
import com.wegotoo.domain.post.repository.response.PostQueryEntity;
import com.wegotoo.domain.user.User;
import com.wegotoo.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
class PostRepositoryImplTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @AfterEach
    void tearDown() {
        postLikeRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("게시글 페이징 조회 테스트")
    void findAllPost() throws Exception {
        // given
        User user = userRepository.save(createUser("user"));

        List<Post> posts = postRepository.saveAll(createPosts(user));

        // when
        List<PostQueryEntity> response = postRepository.findAllPost(0, 4);

        // then
        assertThat(response.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("좋아요한 게시글 페이징 조회 테스트")
    void findAllLikePost() throws Exception {
        // given
        User userA = userRepository.save(createUser("userA"));
        User userB = userRepository.save(createUser("userB"));

        List<Post> posts = postRepository.saveAll(createPosts(userB));
        List<PostLike> postLikes = postLikeRepository.saveAll(createPostLikes(userA, posts));

        // when
        List<PostQueryEntity> response = postRepository.findAllLikePost(userA.getId(), 0, 4);

        // then
        assertThat(response).hasSize(5);
    }

    private User createUser(String username) {
        return User.builder()
                .name(username)
                .email(username + "@gmail.com")
                .build();
    }

    private List<Post> createPosts(User user) {
        return IntStream.range(0, 5)
                .mapToObj(i -> Post.builder()
                        .title("제목 " + i)
                        .view(0)
                        .user(user)
                        .registeredDateTime(LocalDateTime.now())
                        .build())
                .toList();
    }

    private List<PostLike> createPostLikes(User user, List<Post> posts) {
        return IntStream.range(0, 5)
                .mapToObj(i -> PostLike.builder()
                        .user(user)
                        .post(posts.get(i))
                        .build())
                .toList();
    }

}
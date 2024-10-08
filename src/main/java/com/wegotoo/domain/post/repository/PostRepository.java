package com.wegotoo.domain.post.repository;

import com.wegotoo.domain.post.Post;
import com.wegotoo.domain.post.repository.querydsl.PostRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query(
            "select p from Post p join p.user where p.id = :postId"
    )
    Optional<Post> findByIdWithUser(Long postId);
}

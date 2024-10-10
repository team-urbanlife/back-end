package com.wegotoo.domain.post.repository.querydsl;

import static com.wegotoo.domain.like.QPostLike.postLike;
import static com.wegotoo.domain.post.QPost.post;
import static com.wegotoo.domain.user.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wegotoo.domain.post.repository.response.PostQueryEntity;
import com.wegotoo.domain.post.repository.response.QPostQueryEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostQueryEntity> findAllPost(Integer offset, Integer size) {
        return queryFactory.select(new QPostQueryEntity(
                        post.id,
                        post.title,
                        user.name,
                        user.profileImage,
                        post.registeredDateTime,
                        postLike.count()
                ))
                .from(post)
                .join(post.user, user)
                .leftJoin(postLike).on(post.eq(postLike.post))
                .offset(offset)
                .limit(size + 1)
                .groupBy(post.id)
                .orderBy(post.registeredDateTime.desc())
                .fetch();
    }

    @Override
    public List<PostQueryEntity> findAllLikePost(Long userId, Integer offset, Integer size) {
        return queryFactory.select(new QPostQueryEntity(
                        post.id,
                        post.title,
                        user.name,
                        user.profileImage,
                        post.registeredDateTime,
                        postLike.count()
                ))
                .from(post)
                .join(post.user, user)
                .leftJoin(postLike).on(post.eq(postLike.post))
                .where(postLike.user.id.eq(userId))
                .offset(offset)
                .limit(size + 1)
                .groupBy(post.id)
                .orderBy(post.registeredDateTime.desc())
                .fetch();
    }

    @Override
    public Long findPostLikeCount(Long postId) {
        return queryFactory.select(postLike.count())
                .from(post)
                .leftJoin(postLike).on(post.eq(postLike.post))
                .where(post.id.eq(postId))
                .fetchOne();
    }

}

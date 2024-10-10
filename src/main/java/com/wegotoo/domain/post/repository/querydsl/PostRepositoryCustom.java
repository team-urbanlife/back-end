package com.wegotoo.domain.post.repository.querydsl;

import com.wegotoo.domain.post.repository.response.PostQueryEntity;
import java.util.List;

public interface PostRepositoryCustom {
    List<PostQueryEntity> findAllPost(Integer offset, Integer size);

    List<PostQueryEntity> findAllLikePost(Long userId, Integer offset, Integer size);

    Long findPostLikeCount(Long postId);
}

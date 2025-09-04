package com.wegotoo.domain.comment.repository;

import com.wegotoo.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

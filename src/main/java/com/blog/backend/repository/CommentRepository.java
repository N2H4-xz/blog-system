package com.blog.backend.repository;

import com.blog.backend.domain.entity.Comment;
import com.blog.backend.domain.enums.CommentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, CommentStatus status);

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    long countByStatus(CommentStatus status);
}

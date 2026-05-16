package com.blog.backend.repository;

import com.blog.backend.domain.entity.Post;
import com.blog.backend.domain.enums.PostStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Optional<Post> findBySlug(String slug);

    Optional<Post> findBySlugAndStatus(String slug, PostStatus status);

    List<Post> findTop5ByStatusAndIsPinnedOrderByPublishedAtDesc(PostStatus status, boolean isPinned);
}

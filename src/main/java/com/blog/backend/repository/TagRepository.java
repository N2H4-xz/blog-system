package com.blog.backend.repository;

import com.blog.backend.domain.entity.Tag;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySlug(String slug);

    List<Tag> findByIdIn(Collection<Long> ids);

    boolean existsByNameIgnoreCase(String name);
}

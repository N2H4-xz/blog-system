package com.blog.backend.repository;

import com.blog.backend.domain.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);

    Optional<Category> findBySlug(String slug);
}

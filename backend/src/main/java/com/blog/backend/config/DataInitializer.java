package com.blog.backend.config;

import com.blog.backend.domain.entity.Category;
import com.blog.backend.domain.entity.Tag;
import com.blog.backend.domain.entity.User;
import com.blog.backend.domain.entity.UserProfile;
import com.blog.backend.domain.enums.UserRole;
import com.blog.backend.repository.CategoryRepository;
import com.blog.backend.repository.TagRepository;
import com.blog.backend.repository.UserProfileRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.support.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            if (!appProperties.bootstrap().enabled()) {
                return;
            }
            String adminUsername = appProperties.bootstrap().adminUsername();
            String adminPassword = appProperties.bootstrap().adminPassword();
            if (adminUsername == null || adminUsername.isBlank() || adminPassword == null || adminPassword.isBlank()) {
                throw new IllegalStateException("Bootstrap admin credentials must be configured when app.bootstrap.enabled=true");
            }
            if (!userRepository.existsByUsername(adminUsername)) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(UserRole.ADMIN);
                admin = userRepository.save(admin);

                UserProfile profile = new UserProfile();
                profile.setUser(admin);
                profile.setDisplayName("系统管理员");
                profileRepository.save(profile);
            }
            seedCategory("后端开发");
            seedCategory("前端工程");
            seedCategory("学习随笔");
            seedTag("Spring Boot");
            seedTag("React");
            seedTag("TypeScript");
            seedTag("MySQL");
        };
    }

    private void seedCategory(String name) {
        if (!categoryRepository.existsByNameIgnoreCase(name)) {
            Category category = new Category();
            category.setName(name);
            category.setSlug(SlugUtils.toSlug(name));
            categoryRepository.save(category);
        }
    }

    private void seedTag(String name) {
        if (!tagRepository.existsByNameIgnoreCase(name)) {
            Tag tag = new Tag();
            tag.setName(name);
            tag.setSlug(SlugUtils.toSlug(name));
            tagRepository.save(tag);
        }
    }
}

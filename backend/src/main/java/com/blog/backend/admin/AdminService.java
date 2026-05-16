package com.blog.backend.admin;

import com.blog.backend.admin.dto.AdminUserVO;
import com.blog.backend.admin.dto.CategoryRequest;
import com.blog.backend.admin.dto.DashboardVO;
import com.blog.backend.admin.dto.TagRequest;
import com.blog.backend.common.exception.BusinessException;
import com.blog.backend.domain.entity.Category;
import com.blog.backend.domain.entity.Tag;
import com.blog.backend.domain.enums.CommentStatus;
import com.blog.backend.domain.enums.PostStatus;
import com.blog.backend.post.dto.CategoryVO;
import com.blog.backend.post.dto.TagVO;
import com.blog.backend.repository.CategoryRepository;
import com.blog.backend.repository.CommentRepository;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.TagRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.support.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public DashboardVO dashboard() {
        return new DashboardVO(
                userRepository.count(),
                postRepository.count(),
                postRepository.count((root, query, cb) -> cb.equal(root.get("status"), PostStatus.PUBLISHED)),
                commentRepository.countByStatus(CommentStatus.PENDING)
        );
    }

    @Transactional(readOnly = true)
    public java.util.List<AdminUserVO> users() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(user -> new AdminUserVO(
                        user.getId(),
                        user.getUsername(),
                        user.getProfile() != null ? user.getProfile().getDisplayName() : user.getUsername(),
                        user.getRole().name(),
                        user.isEnabled()
                ))
                .toList();
    }

    @Transactional
    public void setUserEnabled(Long userId, boolean enabled) {
        var user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setEnabled(enabled);
    }

    @Transactional
    public CategoryVO createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException(400, "分类已存在");
        }
        Category category = new Category();
        category.setName(request.name());
        category.setSlug(generateUniqueCategorySlug(request.name()));
        category = categoryRepository.save(category);
        return new CategoryVO(category.getId(), category.getName(), category.getSlug());
    }

    @Transactional
    public TagVO createTag(TagRequest request) {
        if (tagRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException(400, "标签已存在");
        }
        Tag tag = new Tag();
        tag.setName(request.name());
        tag.setSlug(generateUniqueTagSlug(request.name()));
        tag = tagRepository.save(tag);
        return new TagVO(tag.getId(), tag.getName(), tag.getSlug());
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException(404, "分类不存在");
        }
        if (postRepository.existsByCategory_Id(id)) {
            throw new BusinessException(400, "分类已被文章使用，无法删除");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new BusinessException(404, "标签不存在");
        }
        if (postRepository.existsByTags_Id(id)) {
            throw new BusinessException(400, "标签已被文章使用，无法删除");
        }
        tagRepository.deleteById(id);
    }

    private String generateUniqueCategorySlug(String name) {
        String base = SlugUtils.toSlug(name);
        String candidate = base;
        int index = 1;
        while (categoryRepository.findBySlug(candidate).isPresent()) {
            candidate = base + "-" + index++;
        }
        return candidate;
    }

    private String generateUniqueTagSlug(String name) {
        String base = SlugUtils.toSlug(name);
        String candidate = base;
        int index = 1;
        while (tagRepository.findBySlug(candidate).isPresent()) {
            candidate = base + "-" + index++;
        }
        return candidate;
    }
}

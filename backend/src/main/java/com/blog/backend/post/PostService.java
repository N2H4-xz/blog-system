package com.blog.backend.post;

import com.blog.backend.common.PageResponse;
import com.blog.backend.common.exception.BusinessException;
import com.blog.backend.domain.entity.Post;
import com.blog.backend.domain.entity.Tag;
import com.blog.backend.domain.entity.User;
import com.blog.backend.domain.enums.PostStatus;
import com.blog.backend.post.dto.CategoryVO;
import com.blog.backend.post.dto.PostCreateRequest;
import com.blog.backend.post.dto.PostDetailVO;
import com.blog.backend.post.dto.PostQueryRequest;
import com.blog.backend.post.dto.PostSummaryVO;
import com.blog.backend.post.dto.PostUpdateRequest;
import com.blog.backend.post.dto.TagVO;
import com.blog.backend.repository.CategoryRepository;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.TagRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.security.AppUserPrincipal;
import com.blog.backend.security.SecurityUtils;
import com.blog.backend.store.ViewCountStore;
import com.blog.backend.support.MarkdownService;
import com.blog.backend.support.SlugUtils;
import com.blog.backend.support.TextSummaryUtils;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final MarkdownService markdownService;
    private final ViewCountStore viewCountStore;

    @Transactional
    public PostDetailVO create(PostCreateRequest request) {
        AppUserPrincipal principal = SecurityUtils.requireCurrentUser();
        User author = userRepository.findById(principal.getId()).orElseThrow(() -> new BusinessException(404, "用户不存在"));
        Post post = new Post();
        applyPostPayload(post, request.title(), request.summary(), request.contentMarkdown(), request.categoryId(), request.tagIds(), request.status());
        post.setAuthor(author);
        post.setSlug(generateUniqueSlug(request.title(), null));
        return toDetail(postRepository.save(post));
    }

    @Transactional
    public PostDetailVO update(Long postId, PostUpdateRequest request) {
        Post post = getOwnedOrAdminPost(postId);
        applyPostPayload(post, request.title(), request.summary(), request.contentMarkdown(), request.categoryId(), request.tagIds(), request.status());
        if (request.pinned() != null && isAdmin()) {
            post.setPinned(request.pinned());
        }
        return toDetail(postRepository.save(post));
    }

    @Transactional
    public void delete(Long postId) {
        postRepository.delete(getOwnedOrAdminPost(postId));
    }

    @Transactional(readOnly = true)
    public PageResponse<PostSummaryVO> myPosts(PostQueryRequest request) {
        AppUserPrincipal principal = SecurityUtils.requireCurrentUser();
        Specification<Post> spec = baseQuery(request, false)
                .and((root, query, cb) -> cb.equal(root.get("author").get("id"), principal.getId()));
        return PageResponse.from(postRepository.findAll(spec, pageRequest(request)).map(this::toSummary));
    }

    @Transactional(readOnly = true)
    public PageResponse<PostSummaryVO> publicPosts(PostQueryRequest request) {
        return PageResponse.from(postRepository.findAll(baseQuery(request, true), pageRequest(request)).map(this::toSummary));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<PostSummaryVO> adminPosts(PostQueryRequest request) {
        return PageResponse.from(postRepository.findAll(baseQuery(request, false), pageRequest(request)).map(this::toSummary));
    }

    @Transactional(readOnly = true)
    public List<PostSummaryVO> pinnedPosts() {
        return postRepository.findTop5ByStatusAndIsPinnedOrderByPublishedAtDesc(PostStatus.PUBLISHED, true)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional
    public PostDetailVO publicDetail(String slug) {
        Post post = postRepository.findBySlugAndStatus(slug, PostStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(404, "文章不存在"));
        viewCountStore.increment(post.getId());
        return toDetail(post);
    }

    @Transactional(readOnly = true)
    public PostDetailVO userEditableDetail(Long id) {
        return toDetail(getOwnedOrAdminPost(id));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void setPinned(Long id, boolean pinned) {
        Post post = postRepository.findById(id).orElseThrow(() -> new BusinessException(404, "文章不存在"));
        post.setPinned(pinned);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void adminDelete(Long id) {
        postRepository.delete(postRepository.findById(id).orElseThrow(() -> new BusinessException(404, "文章不存在")));
    }

    @Transactional
    public void changeStatus(Long id, String status) {
        Post post = getOwnedOrAdminPost(id);
        post.setStatus(parseStatus(status));
        if (post.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void flushViewCounts() {
        viewCountStore.drainAll().forEach((postId, delta) ->
                postRepository.findById(postId).ifPresent(post -> post.setViewCount(post.getViewCount() + delta)));
    }

    @Transactional(readOnly = true)
    public List<CategoryVO> categories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(category -> new CategoryVO(category.getId(), category.getName(), category.getSlug()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TagVO> tags() {
        return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(tag -> new TagVO(tag.getId(), tag.getName(), tag.getSlug()))
                .toList();
    }

    private void applyPostPayload(Post post, String title, String summary, String markdown, Long categoryId, Set<Long> tagIds, String statusText) {
        post.setTitle(title);
        post.setSummary(summary == null || summary.isBlank() ? TextSummaryUtils.summarize(markdown) : summary);
        post.setContentMarkdown(markdown);
        post.setContentHtml(markdownService.render(markdown));
        post.setCategory(categoryId == null ? null : categoryRepository.findById(categoryId).orElseThrow(() -> new BusinessException(404, "分类不存在")));
        var tags = tagRepository.findByIdIn(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new BusinessException(400, "部分标签不存在");
        }
        post.setTags(tags.stream().collect(Collectors.toSet()));
        post.setStatus(parseStatus(statusText));
        if (post.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
    }

    private PostStatus parseStatus(String statusText) {
        if (statusText == null || statusText.isBlank()) {
            return PostStatus.DRAFT;
        }
        try {
            return PostStatus.valueOf(statusText.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(400, "文章状态无效");
        }
    }

    private Post getOwnedOrAdminPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new BusinessException(404, "文章不存在"));
        AppUserPrincipal principal = SecurityUtils.requireCurrentUser();
        if (!"ADMIN".equals(principal.getRole()) && !post.getAuthor().getId().equals(principal.getId())) {
            throw new BusinessException(403, "无权限操作该文章");
        }
        return post;
    }

    private boolean isAdmin() {
        return "ADMIN".equals(SecurityUtils.requireCurrentUser().getRole());
    }

    private Pageable pageRequest(PostQueryRequest request) {
        int page = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 10 : Math.min(request.pageSize(), 20);
        return PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("publishedAt"), Sort.Order.desc("createdAt")));
    }

    private Specification<Post> baseQuery(PostQueryRequest request, boolean onlyPublished) {
        return (root, query, cb) -> {
            query.distinct(true);
            var predicates = cb.conjunction();
            if (onlyPublished) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), PostStatus.PUBLISHED));
            } else if (request.status() != null && !request.status().isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), parseStatus(request.status())));
            }
            if (request.keyword() != null && !request.keyword().isBlank()) {
                String keyword = "%" + request.keyword().trim().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("summary")), keyword)
                ));
            }
            if (request.categoryId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("category").get("id"), request.categoryId()));
            }
            if (request.tagId() != null) {
                var join = root.join("tags", JoinType.LEFT);
                predicates = cb.and(predicates, cb.equal(join.get("id"), request.tagId()));
            }
            return predicates;
        };
    }

    private String generateUniqueSlug(String title, Long postId) {
        String base = SlugUtils.toSlug(title);
        String candidate = base;
        int index = 1;
        while (true) {
            var existing = postRepository.findBySlug(candidate);
            if (existing.isEmpty() || existing.get().getId().equals(postId)) {
                return candidate;
            }
            candidate = base + "-" + index++;
        }
    }

    private PostSummaryVO toSummary(Post post) {
        return new PostSummaryVO(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getStatus().name(),
                post.isPinned(),
                post.getViewCount(),
                post.getAuthor().getProfile() != null ? post.getAuthor().getProfile().getDisplayName() : post.getAuthor().getUsername(),
                post.getCategory() == null ? null : new CategoryVO(post.getCategory().getId(), post.getCategory().getName(), post.getCategory().getSlug()),
                post.getTags().stream().sorted(Comparator.comparing(Tag::getName)).map(tag -> new TagVO(tag.getId(), tag.getName(), tag.getSlug())).toList(),
                post.getPublishedAt(),
                post.getCreatedAt()
        );
    }

    private PostDetailVO toDetail(Post post) {
        long currentViews = post.getViewCount() + viewCountStore.getPendingIncrement(post.getId());
        return new PostDetailVO(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                post.getContentMarkdown(),
                post.getContentHtml(),
                post.getStatus().name(),
                post.isPinned(),
                currentViews,
                post.getAuthor().getProfile() != null ? post.getAuthor().getProfile().getDisplayName() : post.getAuthor().getUsername(),
                post.getAuthor().getId(),
                post.getCategory() == null ? null : new CategoryVO(post.getCategory().getId(), post.getCategory().getName(), post.getCategory().getSlug()),
                post.getTags().stream().sorted(Comparator.comparing(Tag::getName)).map(tag -> new TagVO(tag.getId(), tag.getName(), tag.getSlug())).toList(),
                post.getPublishedAt(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}

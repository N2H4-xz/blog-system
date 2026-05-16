package com.blog.backend.comment;

import com.blog.backend.admin.dto.AdminCommentVO;
import com.blog.backend.comment.dto.CommentCreateRequest;
import com.blog.backend.comment.dto.CommentReplyRequest;
import com.blog.backend.comment.dto.CommentTreeVO;
import com.blog.backend.common.exception.BusinessException;
import com.blog.backend.domain.entity.Comment;
import com.blog.backend.domain.entity.Post;
import com.blog.backend.domain.entity.User;
import com.blog.backend.domain.enums.CommentStatus;
import com.blog.backend.domain.enums.PostStatus;
import com.blog.backend.repository.CommentRepository;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.security.AppUserPrincipal;
import com.blog.backend.security.SecurityUtils;
import com.blog.backend.store.CommentRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRateLimiter commentRateLimiter;

    @Transactional(readOnly = true)
    public List<CommentTreeVO> listByPost(Long postId) {
        AppUserPrincipal currentUser = SecurityUtils.getCurrentUserOrNull();
        List<Comment> comments = currentUser != null && "ADMIN".equals(currentUser.getRole())
                ? commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                : commentRepository.findByPostIdAndStatusOrderByCreatedAtAsc(postId, CommentStatus.APPROVED);
        Map<Long, CommentTreeVO> map = new LinkedHashMap<>();
        List<CommentTreeVO> roots = new ArrayList<>();
        for (Comment comment : comments) {
            CommentTreeVO node = CommentTreeVO.of(
                    comment.getId(),
                    comment.getUser() != null
                            ? (comment.getUser().getProfile() != null ? comment.getUser().getProfile().getDisplayName() : comment.getUser().getUsername())
                            : comment.getGuestName(),
                    comment.getContent(),
                    comment.getStatus().name(),
                    comment.getUser() == null,
                    canDelete(comment, currentUser),
                    comment.getCreatedAt()
            );
            map.put(comment.getId(), node);
            if (comment.getParent() == null) {
                roots.add(node);
            } else {
                CommentTreeVO parent = map.get(comment.getParent().getId());
                if (parent != null) {
                    parent.replies().add(node);
                }
            }
        }
        return roots;
    }

    @Transactional
    public void create(Long postId, CommentCreateRequest request, HttpServletRequest httpServletRequest) {
        Post post = postRepository.findById(postId).filter(item -> item.getStatus() == PostStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(404, "文章不存在"));
        AppUserPrincipal currentUser = SecurityUtils.getCurrentUserOrNull();
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(request.content());
        if (currentUser != null) {
            User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new BusinessException(404, "用户不存在"));
            comment.setUser(user);
            comment.setStatus(CommentStatus.APPROVED);
        } else {
            if (request.guestName() == null || request.guestName().isBlank()) {
                throw new BusinessException(400, "访客评论必须填写昵称");
            }
            String remoteAddress = httpServletRequest.getRemoteAddr();
            if (!commentRateLimiter.allow(remoteAddress == null ? "unknown" : remoteAddress)) {
                throw new BusinessException(429, "评论过于频繁，请稍后再试");
            }
            comment.setGuestName(request.guestName());
            comment.setGuestEmail(request.guestEmail());
            comment.setGuestWebsite(request.guestWebsite());
            comment.setStatus(CommentStatus.PENDING);
        }
        commentRepository.save(comment);
    }

    @Transactional
    public void reply(Long commentId, CommentReplyRequest request) {
        AppUserPrincipal principal = SecurityUtils.requireCurrentUser();
        Comment parent = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(404, "评论不存在"));
        if (parent.getParent() != null) {
            throw new BusinessException(400, "仅支持一级回复");
        }
        User user = userRepository.findById(principal.getId()).orElseThrow(() -> new BusinessException(404, "用户不存在"));
        Comment reply = new Comment();
        reply.setPost(parent.getPost());
        reply.setParent(parent);
        reply.setUser(user);
        reply.setContent(request.content());
        reply.setStatus(CommentStatus.APPROVED);
        commentRepository.save(reply);
    }

    @Transactional
    public void delete(Long commentId) {
        AppUserPrincipal currentUser = SecurityUtils.requireCurrentUser();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(404, "评论不存在"));
        if (!canDelete(comment, currentUser)) {
            throw new BusinessException(403, "无权限删除该评论");
        }
        commentRepository.delete(comment);
    }

    @Transactional
    public void approve(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(404, "评论不存在"));
        comment.setStatus(CommentStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public List<AdminCommentVO> adminComments() {
        return commentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(comment -> new AdminCommentVO(
                        comment.getId(),
                        comment.getPost().getTitle(),
                        comment.getUser() != null
                                ? (comment.getUser().getProfile() != null ? comment.getUser().getProfile().getDisplayName() : comment.getUser().getUsername())
                                : comment.getGuestName(),
                        comment.getContent(),
                        comment.getStatus().name(),
                        comment.getUser() == null,
                        comment.getCreatedAt()
                ))
                .toList();
    }

    private boolean canDelete(Comment comment, AppUserPrincipal currentUser) {
        if (currentUser == null) {
            return false;
        }
        if ("ADMIN".equals(currentUser.getRole())) {
            return true;
        }
        boolean isCommentOwner = comment.getUser() != null && comment.getUser().getId().equals(currentUser.getId());
        boolean isPostOwner = comment.getPost().getAuthor().getId().equals(currentUser.getId());
        return isCommentOwner || isPostOwner;
    }
}

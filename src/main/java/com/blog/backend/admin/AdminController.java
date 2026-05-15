package com.blog.backend.admin;

import com.blog.backend.admin.dto.AdminCommentVO;
import com.blog.backend.admin.dto.AdminUserVO;
import com.blog.backend.admin.dto.CategoryRequest;
import com.blog.backend.admin.dto.DashboardVO;
import com.blog.backend.admin.dto.TagRequest;
import com.blog.backend.comment.CommentService;
import com.blog.backend.common.ApiResponse;
import com.blog.backend.common.PageResponse;
import com.blog.backend.post.PostService;
import com.blog.backend.post.dto.CategoryVO;
import com.blog.backend.post.dto.PostQueryRequest;
import com.blog.backend.post.dto.PostSummaryVO;
import com.blog.backend.post.dto.TagVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardVO> dashboard() {
        return ApiResponse.success(adminService.dashboard());
    }

    @GetMapping("/users")
    public ApiResponse<List<AdminUserVO>> users() {
        return ApiResponse.success(adminService.users());
    }

    @GetMapping("/posts")
    public ApiResponse<PageResponse<PostSummaryVO>> posts(@ModelAttribute PostQueryRequest request) {
        return ApiResponse.success(postService.adminPosts(request));
    }

    @DeleteMapping("/posts/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        postService.adminDelete(id);
        return ApiResponse.successMessage("文章已删除");
    }

    @PatchMapping("/users/{id}/enabled")
    public ApiResponse<Void> toggleUser(@PathVariable Long id, @RequestParam boolean enabled) {
        adminService.setUserEnabled(id, enabled);
        return ApiResponse.successMessage("用户状态已更新");
    }

    @PostMapping("/categories")
    public ApiResponse<CategoryVO> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(adminService.createCategory(request));
    }

    @DeleteMapping("/categories/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ApiResponse.successMessage("分类已删除");
    }

    @PostMapping("/tags")
    public ApiResponse<TagVO> createTag(@Valid @RequestBody TagRequest request) {
        return ApiResponse.success(adminService.createTag(request));
    }

    @DeleteMapping("/tags/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        adminService.deleteTag(id);
        return ApiResponse.successMessage("标签已删除");
    }

    @PatchMapping("/posts/{id}/pin")
    public ApiResponse<Void> pinPost(@PathVariable Long id, @RequestParam boolean pinned) {
        postService.setPinned(id, pinned);
        return ApiResponse.successMessage("置顶状态已更新");
    }

    @PatchMapping("/comments/{id}/approve")
    public ApiResponse<Void> approveComment(@PathVariable Long id) {
        commentService.approve(id);
        return ApiResponse.successMessage("评论已审核通过");
    }

    @GetMapping("/comments")
    public ApiResponse<List<AdminCommentVO>> comments() {
        return ApiResponse.success(commentService.adminComments());
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ApiResponse.successMessage("评论已删除");
    }
}

package com.blog.backend.post;

import com.blog.backend.common.ApiResponse;
import com.blog.backend.common.PageResponse;
import com.blog.backend.post.dto.PostCreateRequest;
import com.blog.backend.post.dto.PostDetailVO;
import com.blog.backend.post.dto.PostQueryRequest;
import com.blog.backend.post.dto.PostSummaryVO;
import com.blog.backend.post.dto.PostUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/posts")
@RequiredArgsConstructor
public class UserPostController {
    private final PostService postService;

    @PostMapping
    public ApiResponse<PostDetailVO> create(@Valid @RequestBody PostCreateRequest request) {
        return ApiResponse.success(postService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PostDetailVO> update(@PathVariable Long id, @Valid @RequestBody PostUpdateRequest request) {
        return ApiResponse.success(postService.update(id, request));
    }

    @GetMapping
    public ApiResponse<PageResponse<PostSummaryVO>> mine(@ModelAttribute PostQueryRequest request) {
        return ApiResponse.success(postService.myPosts(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostDetailVO> editableDetail(@PathVariable Long id) {
        return ApiResponse.success(postService.userEditableDetail(id));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Void> changeStatus(@PathVariable Long id, @RequestParam String status) {
        postService.changeStatus(id, status);
        return ApiResponse.successMessage("状态已更新");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ApiResponse.successMessage("文章已删除");
    }
}

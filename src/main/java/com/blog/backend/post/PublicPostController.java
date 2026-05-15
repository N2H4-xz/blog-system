package com.blog.backend.post;

import com.blog.backend.common.ApiResponse;
import com.blog.backend.common.PageResponse;
import com.blog.backend.post.dto.CategoryVO;
import com.blog.backend.post.dto.PostDetailVO;
import com.blog.backend.post.dto.PostQueryRequest;
import com.blog.backend.post.dto.PostSummaryVO;
import com.blog.backend.post.dto.TagVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicPostController {
    private final PostService postService;

    @GetMapping("/posts")
    public ApiResponse<PageResponse<PostSummaryVO>> posts(@ModelAttribute PostQueryRequest request) {
        return ApiResponse.success(postService.publicPosts(request));
    }

    @GetMapping("/posts/pinned")
    public ApiResponse<List<PostSummaryVO>> pinned() {
        return ApiResponse.success(postService.pinnedPosts());
    }

    @GetMapping("/posts/{slug}")
    public ApiResponse<PostDetailVO> detail(@PathVariable String slug) {
        return ApiResponse.success(postService.publicDetail(slug));
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryVO>> categories() {
        return ApiResponse.success(postService.categories());
    }

    @GetMapping("/tags")
    public ApiResponse<List<TagVO>> tags() {
        return ApiResponse.success(postService.tags());
    }
}

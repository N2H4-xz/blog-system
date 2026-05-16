package com.blog.backend.comment;

import com.blog.backend.comment.dto.CommentCreateRequest;
import com.blog.backend.comment.dto.CommentReplyRequest;
import com.blog.backend.comment.dto.CommentTreeVO;
import com.blog.backend.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<CommentTreeVO>> list(@PathVariable Long postId) {
        return ApiResponse.success(commentService.listByPost(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<Void> create(@PathVariable Long postId, @Valid @RequestBody CommentCreateRequest request, HttpServletRequest httpServletRequest) {
        commentService.create(postId, request, httpServletRequest);
        return ApiResponse.successMessage("评论已提交");
    }

    @PostMapping("/comments/{commentId}/replies")
    public ApiResponse<Void> reply(@PathVariable Long commentId, @Valid @RequestBody CommentReplyRequest request) {
        commentService.reply(commentId, request);
        return ApiResponse.successMessage("回复成功");
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ApiResponse.successMessage("评论已删除");
    }
}

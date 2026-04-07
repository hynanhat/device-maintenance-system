package com.maintenance.controller;

import com.maintenance.dto.request.*;
import com.maintenance.dto.response.*;
import com.maintenance.service.impl.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PageResponse<ForumPostResponse>>> getPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(forumService.getPosts(keyword, category, page, size)));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<ForumPostResponse>> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(forumService.getPostById(id)));
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<ForumPostResponse>> createPost(@Valid @RequestBody ForumPostRequest request) {
        return ResponseEntity.ok(ApiResponse.success(forumService.createPost(request), "Dang bai thanh cong"));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<ForumPostResponse>> updatePost(@PathVariable Long id,
                                                                      @Valid @RequestBody ForumPostRequest request) {
        return ResponseEntity.ok(ApiResponse.success(forumService.updatePost(id, request), "Cap nhat bai thanh cong"));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        forumService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoa bai thanh cong"));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<ForumCommentResponse>>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.success(forumService.getComments(postId)));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<ForumCommentResponse>> createComment(@PathVariable Long postId,
                                                                            @Valid @RequestBody ForumCommentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(forumService.createComment(postId, request), "Binh luan thanh cong"));
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<ForumCommentResponse>> updateComment(@PathVariable Long id,
                                                                            @Valid @RequestBody ForumCommentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(forumService.updateComment(id, request), "Cap nhat binh luan thanh cong"));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        forumService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoa binh luan thanh cong"));
    }
}

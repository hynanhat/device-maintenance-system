package com.maintenance.service.impl;

import com.maintenance.dto.request.ForumCommentRequest;
import com.maintenance.dto.request.ForumPostRequest;
import com.maintenance.dto.response.*;
import com.maintenance.entity.*;
import com.maintenance.exception.BadRequestException;
import com.maintenance.exception.ResourceNotFoundException;
import com.maintenance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumPostRepository postRepository;
    private final ForumCommentRepository commentRepository;
    private final UserService userService;

    public PageResponse<ForumPostResponse> getPosts(String keyword, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("isPinned").descending().and(Sort.by("createdAt").descending()));
        Page<ForumPost> result = postRepository.searchPosts(keyword, category, pageable);
        return PageResponse.of(result.map(this::toPostResponse));
    }

    public ForumPostResponse getPostById(Long id) {
        ForumPost post = findPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return toPostResponse(post);
    }

    @Transactional
    public ForumPostResponse createPost(ForumPostRequest request) {
        User author = userService.getCurrentUser();
        ForumPost post = ForumPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
                .isLocked(false)
                .viewCount(0)
                .author(author)
                .build();
        return toPostResponse(postRepository.save(post));
    }

    @Transactional
    public ForumPostResponse updatePost(Long id, ForumPostRequest request) {
        ForumPost post = findPostById(id);
        User currentUser = userService.getCurrentUser();
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa bài viết này");
        }
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        if (request.getCategory() != null) post.setCategory(request.getCategory());
        if (request.getIsPinned() != null) post.setIsPinned(request.getIsPinned());
        if (request.getIsLocked() != null) post.setIsLocked(request.getIsLocked());
        return toPostResponse(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long id) {
        ForumPost post = findPostById(id);
        User currentUser = userService.getCurrentUser();
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không có quyền xóa bài viết này");
        }
        postRepository.deleteById(id);
    }

    public List<ForumCommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream().map(this::toCommentResponse).toList();
    }

    @Transactional
    public ForumCommentResponse createComment(Long postId, ForumCommentRequest request) {
        ForumPost post = findPostById(postId);
        if (post.getIsLocked()) throw new BadRequestException("Bài viết đã bị khóa bình luận");

        User author = userService.getCurrentUser();
        ForumComment parent = null;
        if (request.getParentCommentId() != null) {
            parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", request.getParentCommentId()));
        }
        ForumComment comment = ForumComment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .parentComment(parent)
                .build();
        return toCommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public ForumCommentResponse updateComment(Long commentId, ForumCommentRequest request) {
        ForumComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        User currentUser = userService.getCurrentUser();
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa comment này");
        }
        comment.setContent(request.getContent());
        return toCommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId) {
        ForumComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        User currentUser = userService.getCurrentUser();
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không có quyền xóa comment này");
        }
        commentRepository.deleteById(commentId);
    }

    private ForumPost findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ForumPost", id));
    }

    private ForumPostResponse toPostResponse(ForumPost p) {
        long commentCount = commentRepository.countByPostId(p.getId());
        return ForumPostResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .content(p.getContent())
                .category(p.getCategory())
                .viewCount(p.getViewCount())
                .isPinned(p.getIsPinned())
                .isLocked(p.getIsLocked())
                .authorId(p.getAuthor() != null ? p.getAuthor().getId() : null)
                .authorName(p.getAuthor() != null ? p.getAuthor().getFullName() : null)
                .authorAvatar(p.getAuthor() != null ? p.getAuthor().getAvatarUrl() : null)
                .commentCount(commentCount)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ForumCommentResponse toCommentResponse(ForumComment c) {
        return ForumCommentResponse.builder()
                .id(c.getId())
                .content(c.getContent())
                .postId(c.getPost() != null ? c.getPost().getId() : null)
                .authorId(c.getAuthor() != null ? c.getAuthor().getId() : null)
                .authorName(c.getAuthor() != null ? c.getAuthor().getFullName() : null)
                .authorAvatar(c.getAuthor() != null ? c.getAuthor().getAvatarUrl() : null)
                .parentCommentId(c.getParentComment() != null ? c.getParentComment().getId() : null)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}

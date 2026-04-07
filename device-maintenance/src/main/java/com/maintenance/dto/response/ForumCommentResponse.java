package com.maintenance.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ForumCommentResponse {
    private Long id;
    private String content;
    private Long postId;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

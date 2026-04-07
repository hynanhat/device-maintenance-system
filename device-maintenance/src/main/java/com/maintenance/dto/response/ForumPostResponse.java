package com.maintenance.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ForumPostResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Integer viewCount;
    private Boolean isPinned;
    private Boolean isLocked;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.maintenance.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ForumCommentRequest {
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
    private Long parentCommentId;
}

package com.maintenance.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ForumPostRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
    private String category;
    private Boolean isPinned;
    private Boolean isLocked;
}

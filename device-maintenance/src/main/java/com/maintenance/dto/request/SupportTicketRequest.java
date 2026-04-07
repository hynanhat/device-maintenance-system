package com.maintenance.dto.request;

import com.maintenance.enums.SeverityLevel;
import com.maintenance.enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SupportTicketRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String subject;
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
    private SeverityLevel priority;
    private TicketStatus status;
    private String adminResponse;
    private Long assignedToId;
}

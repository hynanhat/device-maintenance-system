package com.maintenance.dto.request;

import com.maintenance.enums.IncidentStatus;
import com.maintenance.enums.SeverityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class IncidentRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    private String description;
    @NotNull(message = "Mức độ nghiêm trọng không được để trống")
    private SeverityLevel severity;
    @NotNull(message = "ID thiết bị không được để trống")
    private Long deviceId;
    private Long assignedTechnicianId;
    private LocalDateTime occurredAt;
    private IncidentStatus status;
    private String resolutionNote;
}

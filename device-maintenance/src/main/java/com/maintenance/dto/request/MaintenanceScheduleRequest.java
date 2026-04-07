package com.maintenance.dto.request;

import com.maintenance.enums.MaintenanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class MaintenanceScheduleRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    private String maintenanceType;
    private String description;
    @NotNull(message = "Ngày bảo trì không được để trống")
    private LocalDateTime scheduledDate;
    private Integer estimatedDurationHours;
    @NotNull(message = "ID thiết bị không được để trống")
    private Long deviceId;
    private Long assignedTechnicianId;
    private Double cost;
    private String notes;
    private MaintenanceStatus status;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;
}

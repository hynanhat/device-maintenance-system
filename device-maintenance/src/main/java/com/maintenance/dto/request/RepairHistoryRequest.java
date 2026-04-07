package com.maintenance.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class RepairHistoryRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    private String repairType;
    private String description;
    @NotNull(message = "Ngày sửa chữa không được để trống")
    private LocalDateTime repairDate;
    @NotNull(message = "Chi phí không được để trống")
    private Double cost;
    private String partsReplaced;
    private String technicianNote;
    @NotNull(message = "ID thiết bị không được để trống")
    private Long deviceId;
    private Long incidentId;
    private Long maintenanceScheduleId;
    private Long performedById;
}

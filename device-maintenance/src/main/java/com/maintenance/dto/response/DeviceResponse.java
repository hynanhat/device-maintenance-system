package com.maintenance.dto.response;

import com.maintenance.enums.DeviceStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DeviceResponse {
    private Long id;
    private String deviceCode;
    private String name;
    private String deviceType;
    private String manufacturer;
    private String modelNumber;
    private String serialNumber;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiryDate;
    private Double purchasePrice;
    private DeviceStatus status;
    private String location;
    private String locationDetail;
    private String description;
    private String imageUrl;
    private Integer maintenanceCycleDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long activeIncidentCount;
}

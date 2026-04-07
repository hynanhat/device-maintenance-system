package com.maintenance.dto.request;

import com.maintenance.enums.DeviceStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DeviceRequest {
    @NotBlank(message = "Mã thiết bị không được để trống")
    private String deviceCode;

    @NotBlank(message = "Tên thiết bị không được để trống")
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
    private Integer maintenanceCycleDays;
}

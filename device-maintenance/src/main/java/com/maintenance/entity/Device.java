package com.maintenance.entity;

import com.maintenance.enums.DeviceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "devices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_code", nullable = false, unique = true, length = 50)
    private String deviceCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "device_type", length = 100)
    private String deviceType;

    @Column(length = 100)
    private String manufacturer;

    @Column(name = "model_number", length = 100)
    private String modelNumber;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DeviceStatus status = DeviceStatus.ACTIVE;

    @Column(length = 200)
    private String location;

    @Column(name = "location_detail", length = 500)
    private String locationDetail;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    // Chu kỳ bảo trì (ngày)
    @Column(name = "maintenance_cycle_days")
    @Builder.Default
    private Integer maintenanceCycleDays = 90;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Incident> incidents;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceSchedule> maintenanceSchedules;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RepairHistory> repairHistories;
}

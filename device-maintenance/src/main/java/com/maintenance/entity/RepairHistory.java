package com.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair_histories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepairHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repair_code", unique = true, length = 50)
    private String repairCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "repair_type", length = 100)
    private String repairType; // Sửa chữa, Thay thế linh kiện, Hiệu chỉnh,...

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "repair_date", nullable = false)
    private LocalDateTime repairDate;

    @Column(name = "cost", nullable = false)
    @Builder.Default
    private Double cost = 0.0;

    @Column(name = "parts_replaced", columnDefinition = "TEXT")
    private String partsReplaced;

    @Column(name = "technician_note", columnDefinition = "TEXT")
    private String technicianNote;

    @Column(name = "image_url")
    private String imageUrl;

    // Quan hệ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_schedule_id")
    private MaintenanceSchedule maintenanceSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

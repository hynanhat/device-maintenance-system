package com.maintenance.entity;

import com.maintenance.enums.IncidentStatus;
import com.maintenance.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "incidents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_code", unique = true, length = 50)
    private String incidentCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SeverityLevel severity = SeverityLevel.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IncidentStatus status = IncidentStatus.REPORTED;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_note", columnDefinition = "TEXT")
    private String resolutionNote;

    @Column(name = "image_url")
    private String imageUrl;

    // Quan hệ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_technician_id")
    private User assignedTechnician;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ với RepairHistory
    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RepairHistory> repairHistories;
}

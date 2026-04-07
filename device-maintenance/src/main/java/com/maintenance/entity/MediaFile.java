package com.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "stored_name", nullable = false)
    private String storedName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "entity_type", length = 50)
    private String entityType; // DEVICE, INCIDENT, etc.

    @Column(name = "entity_id")
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

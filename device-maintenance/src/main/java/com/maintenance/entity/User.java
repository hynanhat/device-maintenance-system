package com.maintenance.entity;

import com.maintenance.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ
    @OneToMany(mappedBy = "assignedTechnician", fetch = FetchType.LAZY)
    private List<Incident> assignedIncidents;

    @OneToMany(mappedBy = "assignedTechnician", fetch = FetchType.LAZY)
    private List<MaintenanceSchedule> assignedSchedules;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<ForumPost> forumPosts;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<ForumComment> forumComments;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<SupportTicket> createdTickets;
}

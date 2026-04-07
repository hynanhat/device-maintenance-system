package com.maintenance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "forum_posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String category; // Kỹ thuật, Hỏi đáp, Thông báo,...

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "is_pinned")
    @Builder.Default
    private Boolean isPinned = false;

    @Column(name = "is_locked")
    @Builder.Default
    private Boolean isLocked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumComment> comments;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

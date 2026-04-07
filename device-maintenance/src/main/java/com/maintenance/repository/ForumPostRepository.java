package com.maintenance.repository;

import com.maintenance.entity.ForumPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    @Query("SELECT fp FROM ForumPost fp WHERE " +
           "(:keyword IS NULL OR LOWER(fp.title) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
           "LOWER(fp.content) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND " +
           "(:category IS NULL OR fp.category = :category)")
    Page<ForumPost> searchPosts(@Param("keyword") String keyword,
                                @Param("category") String category,
                                Pageable pageable);

    Page<ForumPost> findByAuthorId(Long authorId, Pageable pageable);
    Page<ForumPost> findByIsPinnedTrue(Pageable pageable);
}

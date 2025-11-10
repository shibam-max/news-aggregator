package com.newsaggregator.repository;

import com.newsaggregator.entity.NewsArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticleEntity, Long> {
    
    Optional<NewsArticleEntity> findByArticleId(String articleId);
    
    Page<NewsArticleEntity> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);
    
    @Query("SELECT n FROM NewsArticleEntity n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<NewsArticleEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    List<NewsArticleEntity> findBySourceAndPublishedAtAfter(String source, LocalDateTime publishedAfter);
    
    @Query("SELECT COUNT(n) FROM NewsArticleEntity n WHERE n.publishedAt >= :startDate")
    long countArticlesSince(@Param("startDate") LocalDateTime startDate);
    
    void deleteByPublishedAtBefore(LocalDateTime cutoffDate);
}
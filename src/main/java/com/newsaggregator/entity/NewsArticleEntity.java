package com.newsaggregator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news_articles")
@Indexed
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    @KeywordField
    private String articleId;
    
    @FullTextField
    private String title;
    
    @FullTextField
    private String description;
    
    @KeywordField
    private String url;
    
    @KeywordField
    private String source;
    
    private LocalDateTime publishedAt;
    
    private String imageUrl;
    
    @FullTextField
    private String author;
    
    @KeywordField
    private String section;
    
    @KeywordField
    private String city;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
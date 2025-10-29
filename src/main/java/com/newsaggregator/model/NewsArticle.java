package com.newsaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticle {
    private String id;
    private String title;
    private String description;
    private String url;
    private String source;
    private LocalDateTime publishedAt;
    private String imageUrl;
    private String author;
    private String section;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NewsArticle article = (NewsArticle) obj;
        return title != null && title.equals(article.title) && 
               url != null && url.equals(article.url);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(title, url);
    }
}
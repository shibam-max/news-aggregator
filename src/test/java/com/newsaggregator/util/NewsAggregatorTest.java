package com.newsaggregator.util;

import com.newsaggregator.model.NewsArticle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsAggregatorTest {
    
    @Test
    void testAggregateAndSort() {
        // Given
        List<NewsArticle> guardianArticles = Arrays.asList(
            NewsArticle.builder()
                .id("guardian_1")
                .title("Apple News")
                .publishedAt(LocalDateTime.now().minusHours(1))
                .build()
        );
        
        List<NewsArticle> nyTimesArticles = Arrays.asList(
            NewsArticle.builder()
                .id("nyt_1")
                .title("Tech News")
                .publishedAt(LocalDateTime.now())
                .build()
        );
        
        // When
        List<NewsArticle> result = NewsAggregator.aggregateAndSort(guardianArticles, nyTimesArticles);
        
        // Then
        assertEquals(2, result.size());
        assertEquals("nyt_1", result.get(0).getId()); // Newest first
        assertEquals("guardian_1", result.get(1).getId());
    }
    
    @Test
    void testDeduplication() {
        // Given
        List<NewsArticle> guardianArticles = Arrays.asList(
            NewsArticle.builder()
                .id("guardian_1")
                .title("Apple News")
                .url("https://example.com/apple")
                .publishedAt(LocalDateTime.now())
                .build()
        );
        
        List<NewsArticle> nyTimesArticles = Arrays.asList(
            NewsArticle.builder()
                .id("nyt_1")
                .title("Apple News") // Same title
                .url("https://different.com/apple")
                .publishedAt(LocalDateTime.now())
                .build()
        );
        
        // When
        List<NewsArticle> result = NewsAggregator.aggregateAndSort(guardianArticles, nyTimesArticles);
        
        // Then
        assertEquals(1, result.size()); // Duplicate removed
    }
}
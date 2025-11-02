package com.newsaggregator.util;

import com.newsaggregator.model.NewsArticle;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewsDeduplicatorTest {
    
    @Test
    void deduplicate_ShouldRemoveDuplicatesByTitle() {
        // Given
        List<NewsArticle> articles = Arrays.asList(
                createArticle("1", "Apple Announces New iPhone", "https://guardian.com/1"),
                createArticle("2", "apple announces new iphone", "https://nytimes.com/2"), // Same title, different case
                createArticle("3", "Different Article", "https://guardian.com/3")
        );
        
        // When
        List<NewsArticle> result = NewsDeduplicator.deduplicate(articles);
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getId().equals("1")));
        assertTrue(result.stream().anyMatch(a -> a.getId().equals("3")));
    }
    
    @Test
    void deduplicate_ShouldRemoveDuplicatesByUrl() {
        // Given
        List<NewsArticle> articles = Arrays.asList(
                createArticle("1", "First Article", "https://example.com/article"),
                createArticle("2", "Second Article", "https://example.com/article"), // Same URL
                createArticle("3", "Third Article", "https://different.com/article")
        );
        
        // When
        List<NewsArticle> result = NewsDeduplicator.deduplicate(articles);
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getId().equals("1")));
        assertTrue(result.stream().anyMatch(a -> a.getId().equals("3")));
    }
    
    @Test
    void deduplicate_ShouldHandleTitleNormalization() {
        // Given
        List<NewsArticle> articles = Arrays.asList(
                createArticle("1", "Apple's New iPhone!", "https://guardian.com/1"),
                createArticle("2", "Apple's New iPhone?", "https://nytimes.com/2"), // Punctuation differences
                createArticle("3", "Apple's    New     iPhone", "https://reuters.com/3") // Extra spaces
        );
        
        // When
        List<NewsArticle> result = NewsDeduplicator.deduplicate(articles);
        
        // Then
        assertEquals(1, result.size()); // All should be considered duplicates
        assertEquals("1", result.get(0).getId());
    }
    
    @Test
    void deduplicate_ShouldHandleEmptyList() {
        // When
        List<NewsArticle> result = NewsDeduplicator.deduplicate(Arrays.asList());
        
        // Then
        assertTrue(result.isEmpty());
    }
    
    @Test
    void deduplicate_ShouldHandleNullTitles() {
        // Given
        List<NewsArticle> articles = Arrays.asList(
                NewsArticle.builder().id("1").title(null).url("https://example.com/1").build(),
                NewsArticle.builder().id("2").title(null).url("https://example.com/2").build(),
                createArticle("3", "Valid Article", "https://example.com/3")
        );
        
        // When
        List<NewsArticle> result = NewsDeduplicator.deduplicate(articles);
        
        // Then
        assertEquals(3, result.size()); // Null titles should not cause duplicates
    }
    
    private NewsArticle createArticle(String id, String title, String url) {
        return NewsArticle.builder()
                .id(id)
                .title(title)
                .description("Test description")
                .url(url)
                .source("Test Source")
                .publishedAt(LocalDateTime.now())
                .build();
    }
}

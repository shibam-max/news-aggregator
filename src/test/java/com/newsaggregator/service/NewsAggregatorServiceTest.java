package com.newsaggregator.service;

import com.newsaggregator.client.GuardianApiClient;
import com.newsaggregator.client.NYTimesApiClient;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsSearchRequest;
import com.newsaggregator.model.NewsSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsAggregatorServiceTest {
    
    @Mock
    private GuardianApiClient guardianClient;
    
    @Mock
    private NYTimesApiClient nyTimesClient;
    
    @Mock
    private OfflineDataService offlineDataService;
    
    @Mock
    private CacheService cacheService;
    
    private NewsAggregatorService newsAggregatorService;
    
    @BeforeEach
    void setUp() {
        newsAggregatorService = new NewsAggregatorService(guardianClient, nyTimesClient, offlineDataService, cacheService);
    }
    
    @Test
    void searchNews_ShouldAggregateFromBothSources() {
        // Given
        NewsSearchRequest request = NewsSearchRequest.builder()
                .keyword("apple")
                .page(1)
                .pageSize(10)
                .build();
        
        List<NewsArticle> guardianArticles = Arrays.asList(
                createTestArticle("1", "Apple News from Guardian", "guardian")
        );
        
        List<NewsArticle> nyTimesArticles = Arrays.asList(
                createTestArticle("2", "Apple News from NYT", "nytimes")
        );
        
        when(cacheService.generateKey(anyString(), anyInt(), anyInt()))
                .thenReturn("test_key");
        when(cacheService.get(anyString()))
                .thenReturn(null); // Cache miss
        when(guardianClient.searchNews(anyString(), anyInt(), anyInt()))
                .thenReturn(Mono.just(guardianArticles));
        when(nyTimesClient.searchNews(anyString(), anyInt(), anyInt()))
                .thenReturn(Mono.just(nyTimesArticles));
        
        // When & Then
        StepVerifier.create(newsAggregatorService.searchNews(request))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals("apple", response.getSearchKeyword());
                    assertEquals(2, response.getArticles().size());
                    assertFalse(response.isOfflineMode());
                })
                .verifyComplete();
    }
    
    @Test
    void searchNews_ShouldHandleOfflineMode() {
        // Given
        NewsSearchRequest request = NewsSearchRequest.builder()
                .keyword("apple")
                .page(1)
                .pageSize(10)
                .offlineMode(true)
                .build();
        
        List<NewsArticle> offlineArticles = Arrays.asList(
                createTestArticle("offline_1", "Offline Apple News", "offline")
        );
        
        when(offlineDataService.getOfflineArticles(anyString()))
                .thenReturn(offlineArticles);
        
        // When & Then
        StepVerifier.create(newsAggregatorService.searchNews(request))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertTrue(response.isOfflineMode());
                    assertEquals(1, response.getArticles().size());
                })
                .verifyComplete();
    }
    
    private NewsArticle createTestArticle(String id, String title, String source) {
        return NewsArticle.builder()
                .id(id)
                .title(title)
                .description("Test description")
                .url("https://example.com/" + id)
                .source(source)
                .publishedAt(LocalDateTime.now())
                .build();
    }
}
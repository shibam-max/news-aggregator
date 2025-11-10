package com.newsaggregator.service;

import com.newsaggregator.client.GuardianApiClient;
import com.newsaggregator.client.NYTimesApiClient;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsSearchRequest;
import com.newsaggregator.model.NewsSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Enhanced News Aggregator Service Tests")
class EnhancedNewsAggregatorServiceTest {
    
    @Mock
    private GuardianApiClient guardianClient;
    
    @Mock
    private NYTimesApiClient nyTimesClient;
    
    @Mock
    private OfflineDataService offlineDataService;
    
    @Mock
    private CacheService cacheService;
    
    @InjectMocks
    private EnhancedNewsAggregatorService newsAggregatorService;
    
    private NewsSearchRequest searchRequest;
    private List<NewsArticle> guardianArticles;
    private List<NewsArticle> nyTimesArticles;
    
    @BeforeEach
    void setUp() {
        searchRequest = NewsSearchRequest.builder()
                .keyword("technology")
                .page(1)
                .pageSize(10)
                .city("london")
                .offlineMode(false)
                .build();
        
        guardianArticles = Arrays.asList(
                NewsArticle.builder()
                        .id("guardian_1")
                        .title("Tech Innovation")
                        .description("Latest tech innovations")
                        .url("https://guardian.com/tech1")
                        .source("The Guardian")
                        .publishedAt(LocalDateTime.now())
                        .build()
        );
        
        nyTimesArticles = Arrays.asList(
                NewsArticle.builder()
                        .id("nytimes_1")
                        .title("Technology Trends")
                        .description("Current technology trends")
                        .url("https://nytimes.com/tech1")
                        .source("New York Times")
                        .publishedAt(LocalDateTime.now())
                        .build()
        );
    }
    
    @Test
    @DisplayName("Should return cached response when available")
    void shouldReturnCachedResponseWhenAvailable() {
        // Given
        NewsSearchResponse cachedResponse = NewsSearchResponse.builder()
                .articles(guardianArticles)
                .searchKeyword("technology")
                .fromCache(true)
                .build();
        
        when(cacheService.generateKey(anyString(), anyInt(), anyInt())).thenReturn("cache_key");
        when(cacheService.get("cache_key")).thenReturn(cachedResponse);
        
        // When
        Mono<NewsSearchResponse> result = newsAggregatorService.searchNews(searchRequest);
        
        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.isFromCache()).isTrue();
                    assertThat(response.getSearchKeyword()).isEqualTo("technology");
                    assertThat(response.getArticles()).hasSize(1);
                })
                .verifyComplete();
        
        verify(guardianClient, never()).searchNews(anyString(), anyInt(), anyInt());
        verify(nyTimesClient, never()).searchNews(anyString(), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("Should aggregate articles from both APIs when cache miss")
    void shouldAggregateArticlesFromBothAPIsWhenCacheMiss() {
        // Given
        when(cacheService.generateKey(anyString(), anyInt(), anyInt())).thenReturn("cache_key");
        when(cacheService.get("cache_key")).thenReturn(null);
        when(guardianClient.searchNews("technology", 1, 10)).thenReturn(Mono.just(guardianArticles));
        when(nyTimesClient.searchNews("technology", 1, 10)).thenReturn(Mono.just(nyTimesArticles));
        
        // When
        Mono<NewsSearchResponse> result = newsAggregatorService.searchNews(searchRequest);
        
        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.isFromCache()).isFalse();
                    assertThat(response.getSearchKeyword()).isEqualTo("technology");
                    assertThat(response.getArticles()).hasSize(2);
                    assertThat(response.getExecutionTimeMs()).isGreaterThan(0);
                })
                .verifyComplete();
        
        verify(cacheService).put(eq("cache_key"), any(NewsSearchResponse.class));
    }
    
    @Test
    @DisplayName("Should fallback to offline mode when APIs fail")
    void shouldFallbackToOfflineModeWhenAPIsFail() {
        // Given
        when(cacheService.generateKey(anyString(), anyInt(), anyInt())).thenReturn("cache_key");
        when(cacheService.get("cache_key")).thenReturn(null);
        when(guardianClient.searchNews("technology", 1, 10)).thenReturn(Mono.error(new RuntimeException("API Error")));
        when(nyTimesClient.searchNews("technology", 1, 10)).thenReturn(Mono.error(new RuntimeException("API Error")));
        when(offlineDataService.getOfflineArticles("technology")).thenReturn(guardianArticles);
        
        // When
        Mono<NewsSearchResponse> result = newsAggregatorService.searchNews(searchRequest);
        
        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.isOfflineMode()).isTrue();
                    assertThat(response.getSearchKeyword()).isEqualTo("technology");
                    assertThat(response.getArticles()).isNotEmpty();
                })
                .verifyComplete();
    }
    
    @Test
    @DisplayName("Should handle offline mode request")
    void shouldHandleOfflineModeRequest() {
        // Given
        searchRequest.setOfflineMode(true);
        when(offlineDataService.getOfflineArticles("technology")).thenReturn(guardianArticles);
        
        // When
        Mono<NewsSearchResponse> result = newsAggregatorService.searchNews(searchRequest);
        
        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.isOfflineMode()).isTrue();
                    assertThat(response.getSearchKeyword()).isEqualTo("technology");
                })
                .verifyComplete();
        
        verify(guardianClient, never()).searchNews(anyString(), anyInt(), anyInt());
        verify(nyTimesClient, never()).searchNews(anyString(), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("Should handle resilience patterns with CompletableFuture")
    void shouldHandleResiliencePatternsWithCompletableFuture() {
        // Given
        when(cacheService.generateKey(anyString(), anyInt(), anyInt())).thenReturn("cache_key");
        when(cacheService.get("cache_key")).thenReturn(null);
        when(guardianClient.searchNews("technology", 1, 10)).thenReturn(Mono.just(guardianArticles));
        when(nyTimesClient.searchNews("technology", 1, 10)).thenReturn(Mono.just(nyTimesArticles));
        
        // When
        CompletableFuture<NewsSearchResponse> result = newsAggregatorService.searchNewsWithResilience(searchRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isDone()).isFalse();
    }
    
    @Test
    @DisplayName("Should use fallback method when circuit breaker activates")
    void shouldUseFallbackMethodWhenCircuitBreakerActivates() {
        // Given
        RuntimeException exception = new RuntimeException("Circuit breaker open");
        when(offlineDataService.getOfflineArticles("technology")).thenReturn(guardianArticles);
        
        // When
        CompletableFuture<NewsSearchResponse> result = newsAggregatorService.fallbackSearchNews(searchRequest, exception);
        
        // Then
        assertThat(result).isNotNull();
        result.thenAccept(response -> {
            assertThat(response.isOfflineMode()).isTrue();
            assertThat(response.getSearchKeyword()).isEqualTo("technology");
        });
    }
}
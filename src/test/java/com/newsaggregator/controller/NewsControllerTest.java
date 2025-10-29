package com.newsaggregator.controller;

import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsSearchRequest;
import com.newsaggregator.model.NewsSearchResponse;
import com.newsaggregator.service.NewsAggregatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(NewsController.class)
class NewsControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private NewsAggregatorService newsAggregatorService;
    
    @Test
    void searchNews_ShouldReturnNewsResponse() {
        // Given
        NewsSearchResponse mockResponse = NewsSearchResponse.builder()
                .articles(Arrays.asList(
                        NewsArticle.builder()
                                .id("1")
                                .title("Test Article")
                                .description("Test Description")
                                .url("https://example.com")
                                .source("Test Source")
                                .publishedAt(LocalDateTime.now())
                                .build()
                ))
                .searchKeyword("apple")
                .currentPage(1)
                .totalPages(1)
                .totalResults(1)
                .pageSize(10)
                .executionTimeMs(100)
                .fromCache(false)
                .offlineMode(false)
                .build();
        
        when(newsAggregatorService.searchNews(any(NewsSearchRequest.class)))
                .thenReturn(Mono.just(mockResponse));
        
        // When & Then
        webTestClient.get()
                .uri("/api/v1/news/search?keyword=apple&page=1&pageSize=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.searchKeyword").isEqualTo("apple")
                .jsonPath("$.articles").isArray()
                .jsonPath("$.articles[0].title").isEqualTo("Test Article");
    }
}
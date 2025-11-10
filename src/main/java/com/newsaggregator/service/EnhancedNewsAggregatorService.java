package com.newsaggregator.service;

import com.newsaggregator.client.GuardianApiClient;
import com.newsaggregator.client.NYTimesApiClient;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsSearchRequest;
import com.newsaggregator.model.NewsSearchResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedNewsAggregatorService {
    
    private final GuardianApiClient guardianClient;
    private final NYTimesApiClient nyTimesClient;
    private final OfflineDataService offlineDataService;
    private final CacheService cacheService;
    
    @CircuitBreaker(name = "news-aggregator", fallbackMethod = "fallbackSearchNews")
    @Retry(name = "news-aggregator")
    @TimeLimiter(name = "news-aggregator")
    public CompletableFuture<NewsSearchResponse> searchNewsWithResilience(NewsSearchRequest request) {
        return searchNews(request).toFuture();
    }
    
    public Mono<NewsSearchResponse> searchNews(NewsSearchRequest request) {
        String cacheKey = cacheService.generateKey(request.getKeyword(), request.getPage(), request.getPageSize());
        
        // Check cache first
        NewsSearchResponse cachedResponse = cacheService.get(cacheKey);
        if (cachedResponse != null) {
            cachedResponse.setFromCache(true);
            return Mono.just(cachedResponse);
        }
        
        long startTime = System.currentTimeMillis();
        
        if (request.isOfflineMode()) {
            return handleOfflineSearch(request, startTime);
        }
        
        return Mono.zip(
                guardianClient.searchNews(request.getKeyword(), request.getPage(), request.getPageSize())
                    .timeout(Duration.ofSeconds(5)),
                nyTimesClient.searchNews(request.getKeyword(), request.getPage(), request.getPageSize())
                    .timeout(Duration.ofSeconds(5))
        )
        .map(tuple -> {
            List<NewsArticle> guardianArticles = tuple.getT1();
            List<NewsArticle> nyTimesArticles = tuple.getT2();
            
            List<NewsArticle> aggregatedArticles = aggregateAndDeduplicateArticles(guardianArticles, nyTimesArticles);
            
            NewsSearchResponse response = buildResponse(request, aggregatedArticles, startTime, false, false);
            
            // Cache the response
            cacheService.put(cacheKey, response);
            
            return response;
        })
        .onErrorResume(error -> {
            log.warn("API call failed, falling back to offline mode", error);
            return handleOfflineSearch(request, startTime);
        });
    }
    
    // Fallback method for Circuit Breaker
    public CompletableFuture<NewsSearchResponse> fallbackSearchNews(NewsSearchRequest request, Exception ex) {
        log.warn("Circuit breaker activated, using fallback method", ex);
        return handleOfflineSearch(request, System.currentTimeMillis()).toFuture();
    }
    
    private Mono<NewsSearchResponse> handleOfflineSearch(NewsSearchRequest request, long startTime) {
        List<NewsArticle> offlineArticles = offlineDataService.getOfflineArticles(request.getKeyword());
        com.newsaggregator.util.NewsPaginator.PaginatedResult paginatedResult = 
            com.newsaggregator.util.NewsPaginator.paginate(offlineArticles, request.getPage(), request.getPageSize());
        
        NewsSearchResponse response = buildResponseWithPagination(request, paginatedResult, startTime, false, true);
        
        return Mono.just(response);
    }
    
    private List<NewsArticle> aggregateAndDeduplicateArticles(List<NewsArticle> guardianArticles, List<NewsArticle> nyTimesArticles) {
        return com.newsaggregator.util.NewsAggregator.aggregateAndSort(guardianArticles, nyTimesArticles);
    }
    
    private NewsSearchResponse buildResponse(NewsSearchRequest request, List<NewsArticle> articles, 
                                           long startTime, boolean fromCache, boolean offlineMode) {
        long executionTime = System.currentTimeMillis() - startTime;
        
        return NewsSearchResponse.builder()
                .articles(articles)
                .searchKeyword(request.getKeyword())
                .city(request.getCity())
                .currentPage(request.getPage())
                .totalPages(calculateTotalPages(articles.size(), request.getPageSize()))
                .totalResults(articles.size())
                .pageSize(request.getPageSize())
                .previousPage(request.getPage() > 1 ? request.getPage() - 1 : null)
                .nextPage(hasNextPage(request.getPage(), articles.size(), request.getPageSize()) ? request.getPage() + 1 : null)
                .executionTimeMs(executionTime)
                .fromCache(fromCache)
                .offlineMode(offlineMode)
                .build();
    }
    
    private NewsSearchResponse buildResponseWithPagination(NewsSearchRequest request, 
                                                          com.newsaggregator.util.NewsPaginator.PaginatedResult paginatedResult,
                                                          long startTime, boolean fromCache, boolean offlineMode) {
        long executionTime = System.currentTimeMillis() - startTime;
        
        return NewsSearchResponse.builder()
                .articles(paginatedResult.getArticles())
                .searchKeyword(request.getKeyword())
                .city(request.getCity())
                .currentPage(request.getPage())
                .totalPages(paginatedResult.getTotalPages())
                .totalResults(paginatedResult.getTotalResults())
                .pageSize(request.getPageSize())
                .previousPage(paginatedResult.getPreviousPage())
                .nextPage(paginatedResult.getNextPage())
                .executionTimeMs(executionTime)
                .fromCache(fromCache)
                .offlineMode(offlineMode)
                .build();
    }
    
    private int calculateTotalPages(int totalResults, int pageSize) {
        return (int) Math.ceil((double) totalResults / pageSize);
    }
    
    private boolean hasNextPage(int currentPage, int totalResults, int pageSize) {
        return currentPage * pageSize < totalResults;
    }
}
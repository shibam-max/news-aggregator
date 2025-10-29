package com.newsaggregator.service;

import com.newsaggregator.client.GuardianApiClient;
import com.newsaggregator.client.NYTimesApiClient;
import com.newsaggregator.model.NewsArticle;
import com.newsaggregator.model.NewsSearchRequest;
import com.newsaggregator.model.NewsSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsAggregatorService {
    
    private final GuardianApiClient guardianClient;
    private final NYTimesApiClient nyTimesClient;
    private final OfflineDataService offlineDataService;
    private final CacheService cacheService;
    
    public Mono<NewsSearchResponse> searchNews(NewsSearchRequest request) {
        String cacheKey = cacheService.generateKey(request.getKeyword(), request.getPage(), request.getPageSize());
        
        // Check custom cache first
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
                guardianClient.searchNews(request.getKeyword(), request.getPage(), request.getPageSize()),
                nyTimesClient.searchNews(request.getKeyword(), request.getPage(), request.getPageSize())
        )
        .map(tuple -> {
            List<NewsArticle> guardianArticles = tuple.getT1();
            List<NewsArticle> nyTimesArticles = tuple.getT2();
            
            List<NewsArticle> aggregatedArticles = aggregateAndDeduplicateArticles(guardianArticles, nyTimesArticles);
            
            NewsSearchResponse response = buildResponse(request, aggregatedArticles, startTime, false, false);
            
            // Cache the response using custom cache
            cacheService.put(cacheKey, response);
            
            return response;
        })
        .onErrorResume(error -> {
            log.warn("API call failed, falling back to offline mode", error);
            return handleOfflineSearch(request, startTime);
        });
    }
    
    private Mono<NewsSearchResponse> handleOfflineSearch(NewsSearchRequest request, long startTime) {
        List<NewsArticle> offlineArticles = offlineDataService.getOfflineArticles(request.getKeyword());
        com.newsaggregator.util.NewsPaginator.PaginatedResult paginatedResult = 
            com.newsaggregator.util.NewsPaginator.paginate(offlineArticles, request.getPage(), request.getPageSize());
        
        NewsSearchResponse response = buildResponseWithPagination(request, paginatedResult, startTime, false, true);
        
        return Mono.just(response);
    }
    
    private List<NewsArticle> aggregateAndDeduplicateArticles(List<NewsArticle> guardianArticles, List<NewsArticle> nyTimesArticles) {
        // Use custom aggregation logic without 3rd party libraries
        return com.newsaggregator.util.NewsAggregator.aggregateAndSort(guardianArticles, nyTimesArticles);
    }
    
    private List<NewsArticle> paginateArticles(List<NewsArticle> articles, int page, int pageSize) {
        // Use custom pagination logic without 3rd party libraries
        com.newsaggregator.util.NewsPaginator.PaginatedResult result = 
            com.newsaggregator.util.NewsPaginator.paginate(articles, page, pageSize);
        return result.getArticles();
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
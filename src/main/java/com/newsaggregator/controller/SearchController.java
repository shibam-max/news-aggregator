package com.newsaggregator.controller;

import com.newsaggregator.entity.NewsArticleEntity;
import com.newsaggregator.service.HibernateSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Advanced search operations using Hibernate Search")
public class SearchController {
    
    private final HibernateSearchService hibernateSearchService;
    
    @GetMapping("/fulltext")
    @Operation(summary = "Full-text search", description = "Performs full-text search across news articles")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<List<NewsArticleEntity>> fullTextSearch(
            @Parameter(description = "Search term") 
            @RequestParam @NotBlank String term,
            @Parameter(description = "Maximum number of results")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int maxResults) {
        
        List<NewsArticleEntity> results = hibernateSearchService.searchArticles(term, maxResults);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/fuzzy")
    @Operation(summary = "Fuzzy search", description = "Performs fuzzy search with typo tolerance")
    @ApiResponse(responseCode = "200", description = "Fuzzy search completed successfully")
    public ResponseEntity<List<NewsArticleEntity>> fuzzySearch(
            @Parameter(description = "Search term") 
            @RequestParam @NotBlank String term,
            @Parameter(description = "Maximum number of results")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int maxResults) {
        
        List<NewsArticleEntity> results = hibernateSearchService.fuzzySearchArticles(term, maxResults);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/by-source")
    @Operation(summary = "Search by source", description = "Searches articles from specific news source")
    @ApiResponse(responseCode = "200", description = "Source-based search completed successfully")
    public ResponseEntity<List<NewsArticleEntity>> searchBySource(
            @Parameter(description = "News source") 
            @RequestParam @NotBlank String source,
            @Parameter(description = "Search keyword") 
            @RequestParam @NotBlank String keyword,
            @Parameter(description = "Maximum number of results")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int maxResults) {
        
        List<NewsArticleEntity> results = hibernateSearchService.searchBySourceAndKeyword(source, keyword, maxResults);
        return ResponseEntity.ok(results);
    }
}
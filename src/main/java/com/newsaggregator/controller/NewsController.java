package com.newsaggregator.controller;

import com.newsaggregator.model.NewsSearchRequest;
import com.newsaggregator.model.NewsSearchResponse;
import com.newsaggregator.service.NewsAggregatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
@Tag(name = "News Aggregator", description = "News search and aggregation API")
@CrossOrigin(origins = "*")
public class NewsController {
    
    private final NewsAggregatorService newsAggregatorService;
    
    @GetMapping("/search")
    @Operation(summary = "Search news articles", 
               description = "Search and aggregate news from Guardian and NY Times APIs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved news articles"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<NewsSearchResponse>> searchNews(
            @Parameter(description = "Search keyword", required = true)
            @RequestParam String keyword,
            
            @Parameter(description = "Page number (default: 1)")
            @RequestParam(defaultValue = "1") int page,
            
            @Parameter(description = "Page size (default: 10)")
            @RequestParam(defaultValue = "10") int pageSize,
            
            @Parameter(description = "City filter")
            @RequestParam(required = false) String city,
            
            @Parameter(description = "Enable offline mode")
            @RequestParam(defaultValue = "false") boolean offlineMode) {
        
        NewsSearchRequest request = NewsSearchRequest.builder()
                .keyword(keyword)
                .page(page)
                .pageSize(pageSize)
                .city(city)
                .offlineMode(offlineMode)
                .build();
        
        return newsAggregatorService.searchNews(request)
                .map(ResponseEntity::ok);
    }
    
    @PostMapping("/search")
    @Operation(summary = "Search news articles with POST", 
               description = "Search and aggregate news using POST request body")
    public Mono<ResponseEntity<NewsSearchResponse>> searchNewsPost(
            @Valid @RequestBody NewsSearchRequest request) {
        
        return newsAggregatorService.searchNews(request)
                .map(ResponseEntity::ok);
    }
}
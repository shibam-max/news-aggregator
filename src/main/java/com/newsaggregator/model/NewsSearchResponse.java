package com.newsaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSearchResponse {
    private List<NewsArticle> articles;
    private String searchKeyword;
    private String city;
    private int currentPage;
    private int totalPages;
    private long totalResults;
    private int pageSize;
    private Integer previousPage;
    private Integer nextPage;
    private long executionTimeMs;
    private boolean fromCache;
    private boolean offlineMode;
}
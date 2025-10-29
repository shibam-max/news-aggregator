package com.newsaggregator.util;

import com.newsaggregator.model.NewsArticle;

import java.util.ArrayList;
import java.util.List;

public class NewsPaginator {
    
    public static class PaginatedResult {
        private final List<NewsArticle> articles;
        private final int totalPages;
        private final long totalResults;
        private final Integer previousPage;
        private final Integer nextPage;
        
        public PaginatedResult(List<NewsArticle> articles, int totalPages, long totalResults, 
                              Integer previousPage, Integer nextPage) {
            this.articles = articles;
            this.totalPages = totalPages;
            this.totalResults = totalResults;
            this.previousPage = previousPage;
            this.nextPage = nextPage;
        }
        
        public List<NewsArticle> getArticles() { return articles; }
        public int getTotalPages() { return totalPages; }
        public long getTotalResults() { return totalResults; }
        public Integer getPreviousPage() { return previousPage; }
        public Integer getNextPage() { return nextPage; }
    }
    
    public static PaginatedResult paginate(List<NewsArticle> allArticles, int page, int pageSize) {
        if (allArticles == null || allArticles.isEmpty()) {
            return new PaginatedResult(new ArrayList<>(), 0, 0, null, null);
        }
        
        int totalResults = allArticles.size();
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);
        
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalResults);
        
        List<NewsArticle> pageArticles = new ArrayList<>();
        if (startIndex < totalResults) {
            pageArticles = allArticles.subList(startIndex, endIndex);
        }
        
        Integer previousPage = page > 1 ? page - 1 : null;
        Integer nextPage = page < totalPages ? page + 1 : null;
        
        return new PaginatedResult(pageArticles, totalPages, totalResults, previousPage, nextPage);
    }
}
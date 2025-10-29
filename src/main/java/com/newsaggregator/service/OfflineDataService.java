package com.newsaggregator.service;

import com.newsaggregator.model.NewsArticle;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfflineDataService {
    
    private final List<NewsArticle> offlineArticles;
    
    public OfflineDataService() {
        this.offlineArticles = initializeOfflineData();
    }
    
    public List<NewsArticle> getOfflineArticles(String keyword) {
        return offlineArticles.stream()
                .filter(article -> containsKeyword(article, keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private boolean containsKeyword(NewsArticle article, String keyword) {
        return article.getTitle().toLowerCase().contains(keyword) ||
               article.getDescription().toLowerCase().contains(keyword);
    }
    
    private List<NewsArticle> initializeOfflineData() {
        List<NewsArticle> articles = new ArrayList<>();
        
        // Sample offline data for demonstration
        articles.add(NewsArticle.builder()
                .id("offline_1")
                .title("Apple Announces New iPhone Features")
                .description("Apple unveils innovative features for the latest iPhone model")
                .url("https://example.com/apple-iphone-news")
                .source("Tech News Offline")
                .publishedAt(LocalDateTime.now().minusDays(1))
                .author("Tech Reporter")
                .section("Technology")
                .build());
                
        articles.add(NewsArticle.builder()
                .id("offline_2")
                .title("Global Technology Market Trends")
                .description("Analysis of current technology market trends and predictions")
                .url("https://example.com/tech-market-trends")
                .source("Business News Offline")
                .publishedAt(LocalDateTime.now().minusDays(2))
                .author("Market Analyst")
                .section("Business")
                .build());
                
        articles.add(NewsArticle.builder()
                .id("offline_3")
                .title("Climate Change Impact on Agriculture")
                .description("Study reveals significant impact of climate change on global agriculture")
                .url("https://example.com/climate-agriculture")
                .source("Science News Offline")
                .publishedAt(LocalDateTime.now().minusDays(3))
                .author("Science Correspondent")
                .section("Environment")
                .build());
                
        return articles;
    }
}
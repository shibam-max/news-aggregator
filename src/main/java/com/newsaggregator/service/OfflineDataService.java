package com.newsaggregator.service;

import com.newsaggregator.model.NewsArticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OfflineDataService {
    
    private final List<NewsArticle> offlineArticles;
    
    public OfflineDataService() {
        this.offlineArticles = initializeOfflineData();
    }
    
    public List<NewsArticle> getOfflineArticles(String keyword) {
        log.debug("🔍 [DEBUG-17] OfflineDataService.getOfflineArticles() - keyword: {}", keyword);
        log.debug("🔍 [DEBUG-18] Total offline articles available: {}", offlineArticles.size());
        
        List<NewsArticle> filteredArticles = offlineArticles.stream()
                .filter(article -> containsKeyword(article, keyword.toLowerCase()))
                .collect(Collectors.toList());
        
        log.debug("🔍 [DEBUG-19] Filtered articles for keyword '{}': {}", keyword, filteredArticles.size());
        return filteredArticles;
    }
    
    private boolean containsKeyword(NewsArticle article, String keyword) {
        return article.getTitle().toLowerCase().contains(keyword) ||
               article.getDescription().toLowerCase().contains(keyword);
    }
    
    private List<NewsArticle> initializeOfflineData() {
        List<NewsArticle> articles = new ArrayList<>();
        
        // Initialize offline data cache
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
                
        articles.add(NewsArticle.builder()
                .id("offline_4")
                .title("Global Business Markets Show Strong Growth")
                .description("International business markets demonstrate resilience with strong quarterly growth across sectors")
                .url("https://example.com/business-growth")
                .source("Business News Offline")
                .publishedAt(LocalDateTime.now().minusDays(1))
                .author("Business Reporter")
                .section("Business")
                .build());
                
        articles.add(NewsArticle.builder()
                .id("offline_5")
                .title("London Financial District Expansion")
                .description("London's financial business district announces major expansion plans for next year")
                .url("https://example.com/london-business")
                .source("Finance News Offline")
                .publishedAt(LocalDateTime.now().minusDays(2))
                .author("Finance Correspondent")
                .section("Finance")
                .build());
                
        return articles;
    }
}
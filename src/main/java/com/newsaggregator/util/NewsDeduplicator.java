package com.newsaggregator.util;

import com.newsaggregator.model.NewsArticle;

import java.util.*;

public class NewsDeduplicator {
    
    public static List<NewsArticle> deduplicate(List<NewsArticle> articles) {
        Set<String> seenTitles = new HashSet<>();
        Set<String> seenUrls = new HashSet<>();
        List<NewsArticle> uniqueArticles = new ArrayList<>();
        
        for (NewsArticle article : articles) {
            String normalizedTitle = normalizeTitle(article.getTitle());
            String url = article.getUrl();
            
            if (!seenTitles.contains(normalizedTitle) && !seenUrls.contains(url)) {
                seenTitles.add(normalizedTitle);
                seenUrls.add(url);
                uniqueArticles.add(article);
            }
        }
        
        return uniqueArticles;
    }
    
    private static String normalizeTitle(String title) {
        if (title == null) return "";
        return title.toLowerCase()
                   .replaceAll("[^a-zA-Z0-9\\s]", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
}
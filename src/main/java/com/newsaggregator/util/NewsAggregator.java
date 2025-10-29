package com.newsaggregator.util;

import com.newsaggregator.model.NewsArticle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewsAggregator {
    
    public static List<NewsArticle> aggregateAndSort(List<NewsArticle> guardianArticles, 
                                                    List<NewsArticle> nyTimesArticles) {
        List<NewsArticle> allArticles = new ArrayList<>();
        
        if (guardianArticles != null) {
            allArticles.addAll(guardianArticles);
        }
        
        if (nyTimesArticles != null) {
            allArticles.addAll(nyTimesArticles);
        }
        
        // Remove duplicates using custom logic
        List<NewsArticle> uniqueArticles = NewsDeduplicator.deduplicate(allArticles);
        
        // Sort by publication date (newest first) using custom comparator
        Collections.sort(uniqueArticles, new Comparator<NewsArticle>() {
            @Override
            public int compare(NewsArticle a1, NewsArticle a2) {
                LocalDateTime date1 = a1.getPublishedAt();
                LocalDateTime date2 = a2.getPublishedAt();
                
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                
                return date2.compareTo(date1); // Newest first
            }
        });
        
        return uniqueArticles;
    }
}
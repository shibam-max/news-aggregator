package com.newsaggregator.service;

import com.newsaggregator.model.NewsSearchResponse;
import com.newsaggregator.util.SimpleCache;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    
    private final SimpleCache<String, NewsSearchResponse> cache;
    
    public CacheService() {
        // 5 minutes TTL
        this.cache = new SimpleCache<>(5 * 60 * 1000);
    }
    
    public void put(String key, NewsSearchResponse response) {
        cache.put(key, response);
    }
    
    public NewsSearchResponse get(String key) {
        return cache.get(key);
    }
    
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
    
    public String generateKey(String keyword, int page, int pageSize) {
        return keyword + "_" + page + "_" + pageSize;
    }
}
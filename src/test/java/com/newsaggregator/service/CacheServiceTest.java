package com.newsaggregator.service;

import com.newsaggregator.model.NewsSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheServiceTest {
    
    private CacheService cacheService;
    
    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
    }
    
    @Test
    void testCacheOperations() {
        // Given
        String key = "test_key";
        NewsSearchResponse response = NewsSearchResponse.builder()
                .searchKeyword("apple")
                .currentPage(1)
                .build();
        
        // When
        cacheService.put(key, response);
        
        // Then
        assertTrue(cacheService.containsKey(key));
        NewsSearchResponse cached = cacheService.get(key);
        assertNotNull(cached);
        assertEquals("apple", cached.getSearchKeyword());
    }
    
    @Test
    void testGenerateKey() {
        // When
        String key = cacheService.generateKey("apple", 1, 10);
        
        // Then
        assertEquals("apple_1_10", key);
    }
}
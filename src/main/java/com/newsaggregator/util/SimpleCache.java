package com.newsaggregator.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleCache<K, V> {
    
    private final ConcurrentMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;
    
    public SimpleCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }
    
    public void put(K key, V value) {
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry<>(value, expiryTime));
    }
    
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        
        if (System.currentTimeMillis() > entry.expiryTime) {
            cache.remove(key);
            return null;
        }
        
        return entry.value;
    }
    
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    
    public void clear() {
        cache.clear();
    }
    
    private static class CacheEntry<V> {
        final V value;
        final long expiryTime;
        
        CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }
}
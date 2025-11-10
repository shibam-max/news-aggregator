import { useState, useCallback, useRef } from 'react';
import { newsService } from '../services/newsService';

export const useNewsSearch = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const abortControllerRef = useRef(null);
  
  const searchNews = useCallback(async (searchParams) => {
    // Cancel previous request if still pending
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
    
    // Create new abort controller for this request
    abortControllerRef.current = new AbortController();
    
    setLoading(true);
    setError(null);
    
    try {
      const response = await newsService.searchNews(searchParams, {
        signal: abortControllerRef.current.signal
      });
      
      setData(response);
      return response;
    } catch (err) {
      if (err.name !== 'AbortError') {
        setError(err.message || 'An error occurred while searching news');
        console.error('News search error:', err);
      }
      throw err;
    } finally {
      setLoading(false);
      abortControllerRef.current = null;
    }
  }, []);
  
  const clearData = useCallback(() => {
    setData(null);
    setError(null);
  }, []);
  
  const clearError = useCallback(() => {
    setError(null);
  }, []);
  
  // Cleanup on unmount
  const cleanup = useCallback(() => {
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
  }, []);
  
  return {
    data,
    loading,
    error,
    searchNews,
    clearData,
    clearError,
    cleanup
  };
};
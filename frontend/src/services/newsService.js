const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Validate API base URL
if (!API_BASE_URL) {
  throw new Error('API base URL is not configured');
}

class NewsService {
  constructor() {
    this.baseURL = `${API_BASE_URL}/api/v1/news`;
  }
  
  async searchNews(params, options = {}) {
    const { signal } = options;
    
    try {
      const queryParams = new URLSearchParams();
      
      // Add required parameters
      if (params.keyword) queryParams.append('keyword', params.keyword);
      if (params.page) queryParams.append('page', params.page);
      if (params.pageSize) queryParams.append('pageSize', params.pageSize);
      if (params.city) queryParams.append('city', params.city);
      if (params.offlineMode !== undefined) queryParams.append('offlineMode', params.offlineMode);
      
      const url = `${this.baseURL}/search?${queryParams.toString()}`;
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        signal
      });
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      if (error.name === 'AbortError') {
        throw error;
      }
      
      console.error('News search failed:', error);
      throw new Error(error.message || 'Failed to search news');
    }
  }
  
  async searchNewsPost(params, options = {}) {
    const { signal } = options;
    
    try {
      const response = await fetch(`${this.baseURL}/search`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'X-Requested-With': 'XMLHttpRequest'
        },
        credentials: 'same-origin',
        body: JSON.stringify(params),
        signal
      });
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      if (error.name === 'AbortError') {
        throw error;
      }
      
      console.error('News search (POST) failed:', error);
      throw new Error(error.message || 'Failed to search news');
    }
  }
  
  async getHealthCheck() {
    try {
      const response = await fetch(`${API_BASE_URL}/actuator/health`);
      return await response.json();
    } catch (error) {
      console.error('Health check failed:', error);
      throw error;
    }
  }
}

export const newsService = new NewsService();
# News Aggregator API Specification

## OpenAPI 3.0 Specification

### Base Information
- **Title**: News Aggregator API
- **Version**: 1.0.0
- **Description**: Enterprise News Aggregator microservice that aggregates news from Guardian and NY Times APIs
- **Base URL**: `http://localhost:8080`
- **Contact**: See GitHub repository for issues

### Authentication
No authentication required for public endpoints.

### Error Codes

| Code | Description | Response Format |
|------|-------------|-----------------|
| 200 | Success | NewsSearchResponse |
| 400 | Bad Request - Invalid parameters | ErrorResponse |
| 404 | Not Found | ErrorResponse |
| 500 | Internal Server Error | ErrorResponse |
| 503 | Service Unavailable - APIs down | ErrorResponse (with offline fallback) |

### Error Response Format
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "keyword": "Search keyword is required",
    "page": "Page number must be greater than 0"
  }
}
```

## Endpoints

### 1. Search News (GET)

**Endpoint**: `GET /api/v1/news/search`

**Description**: Search and aggregate news articles from Guardian and NY Times APIs with pagination support.

**Parameters**:
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| keyword | string | Yes | - | Search keyword (e.g., "apple") |
| page | integer | No | 1 | Page number (min: 1) |
| pageSize | integer | No | 10 | Results per page (min: 1, max: 100) |
| city | string | No | - | City filter for location-based news |
| offlineMode | boolean | No | false | Enable offline mode |

**Example Request**:
```http
GET /api/v1/news/search?keyword=apple&page=1&pageSize=10&city=london&offlineMode=false
Host: localhost:8080
Accept: application/json
```

**Example Response**:
```json
{
  "articles": [
    {
      "id": "guardian_123456",
      "title": "Apple Announces Revolutionary New Features",
      "description": "Apple unveils groundbreaking innovations in their latest product lineup...",
      "url": "https://www.theguardian.com/technology/2024/01/15/apple-announces-new-features",
      "source": "The Guardian",
      "publishedAt": "2024-01-15T10:30:00",
      "imageUrl": "https://media.guim.co.uk/image.jpg",
      "author": "Tech Reporter",
      "section": "Technology"
    },
    {
      "id": "nyt_789012",
      "title": "Apple's Market Impact Continues to Grow",
      "description": "Analysis of Apple's continued influence on global technology markets...",
      "url": "https://www.nytimes.com/2024/01/15/business/apple-market-impact.html",
      "source": "The New York Times",
      "publishedAt": "2024-01-15T09:15:00",
      "imageUrl": "https://static01.nyt.com/images/image.jpg",
      "author": "Business Correspondent",
      "section": "Business"
    }
  ],
  "searchKeyword": "apple",
  "city": "london",
  "currentPage": 1,
  "totalPages": 5,
  "totalResults": 47,
  "pageSize": 10,
  "previousPage": null,
  "nextPage": 2,
  "executionTimeMs": 245,
  "fromCache": false,
  "offlineMode": false
}
```

### 2. Search News (POST)

**Endpoint**: `POST /api/v1/news/search`

**Description**: Search news using POST request body for complex search parameters.

**Request Body**:
```json
{
  "keyword": "apple",
  "page": 1,
  "pageSize": 10,
  "city": "london",
  "offlineMode": false
}
```

**Example Request**:
```http
POST /api/v1/news/search
Host: localhost:8080
Content-Type: application/json
Accept: application/json

{
  "keyword": "technology",
  "page": 2,
  "pageSize": 20,
  "city": "san francisco",
  "offlineMode": false
}
```

**Response**: Same format as GET endpoint.

## Data Models

### NewsSearchRequest
```json
{
  "keyword": "string (required, min: 1 char)",
  "page": "integer (optional, min: 1, default: 1)",
  "pageSize": "integer (optional, min: 1, max: 100, default: 10)",
  "city": "string (optional)",
  "offlineMode": "boolean (optional, default: false)"
}
```

### NewsSearchResponse
```json
{
  "articles": "Array<NewsArticle>",
  "searchKeyword": "string",
  "city": "string (optional)",
  "currentPage": "integer",
  "totalPages": "integer",
  "totalResults": "integer",
  "pageSize": "integer",
  "previousPage": "integer (nullable)",
  "nextPage": "integer (nullable)",
  "executionTimeMs": "long",
  "fromCache": "boolean",
  "offlineMode": "boolean"
}
```

### NewsArticle
```json
{
  "id": "string (unique identifier)",
  "title": "string",
  "description": "string",
  "url": "string (article URL)",
  "source": "string (Guardian/NY Times)",
  "publishedAt": "string (ISO 8601 datetime)",
  "imageUrl": "string (optional)",
  "author": "string (optional)",
  "section": "string (optional)"
}
```

## Health Check Endpoints

### Application Health
```http
GET /actuator/health
```

**Response**:
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91943821312,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Application Info
```http
GET /actuator/info
```

### Application Metrics
```http
GET /actuator/metrics
```

## Rate Limiting

- **Default**: 100 requests per minute per IP
- **Burst**: Up to 10 requests per second
- **Headers**: 
  - `X-RateLimit-Limit`: Request limit
  - `X-RateLimit-Remaining`: Remaining requests
  - `X-RateLimit-Reset`: Reset time

## Caching

- **Cache Duration**: 5 minutes for search results
- **Cache Key**: `keyword_page_pageSize`
- **Cache Headers**:
  - `X-Cache-Status`: HIT/MISS
  - `Cache-Control`: max-age=300

## CORS Policy

- **Allowed Origins**: `*` (configurable)
- **Allowed Methods**: GET, POST, OPTIONS
- **Allowed Headers**: Content-Type, Authorization
- **Max Age**: 3600 seconds

## API Versioning

- **Current Version**: v1
- **URL Pattern**: `/api/v1/`
- **Backward Compatibility**: Maintained for major versions

## Performance Characteristics

- **Average Response Time**: < 500ms
- **95th Percentile**: < 1000ms
- **Throughput**: 1000+ requests/second
- **Availability**: 99.9% uptime SLA

## Security Headers

All responses include security headers:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000`

## Offline Mode Behavior

When `offlineMode=true` or external APIs are unavailable:
- Returns cached/static news articles
- Response includes `"offlineMode": true`
- Reduced article count but guaranteed response
- Fallback data includes sample articles for testing

## Pagination Behavior

- **Page Numbers**: 1-based indexing
- **Navigation**: `previousPage` and `nextPage` fields
- **Bounds**: Automatically handled, no errors for out-of-range pages
- **Empty Results**: Returns empty array with proper pagination metadata

## Content Aggregation Logic

1. **Parallel API Calls**: Guardian and NY Times called simultaneously
2. **Deduplication**: Articles with same title or URL are merged
3. **Sorting**: Results sorted by publication date (newest first)
4. **Pagination**: Applied after aggregation and deduplication
5. **Fallback**: Automatic offline mode if APIs fail

## Monitoring and Observability

### Custom Metrics
- `news.api.calls.total`: Total API calls made
- `news.api.calls.success`: Successful API calls
- `news.api.calls.failure`: Failed API calls
- `news.cache.hits`: Cache hit count
- `news.cache.misses`: Cache miss count
- `news.response.time`: Response time histogram

### Log Levels
- **INFO**: Request/response logging
- **DEBUG**: Detailed API call information
- **WARN**: API failures, fallback activation
- **ERROR**: Unexpected errors, system issues

This API specification documents the endpoints and provides guidance on expected behavior and error handling.
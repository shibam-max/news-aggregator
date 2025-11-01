# News Aggregator Microservice

A microservice that aggregates news articles from Guardian and New York Times APIs. Built with Spring Boot and includes pagination, offline fallback, and duplicate article removal.

## Features

- Searches and combines articles from Guardian UK and NY Times US APIs
- Removes duplicate articles based on title and URL matching
- Configurable pagination with default page sizes
- Offline mode when external APIs are unavailable
- Simple caching implementation for improved response times
- Built with Spring WebFlux for reactive programming
- Basic security configuration and input validation
- Health monitoring and application metrics

## Architecture

### System Design
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   React UI      │────│  Spring Boot     │────│  Guardian API   │
│   (Frontend)    │    │  (Backend)       │    │                 │
└─────────────────┘    │                  │    └─────────────────┘
                       │  - Aggregation   │    
                       │  - Caching       │    ┌─────────────────┐
                       │  - Offline Mode  │────│  NY Times API   │
                       │  - Validation    │    │                 │
                       └──────────────────┘    └─────────────────┘
```

### Sequence Diagram
For detailed sequence diagrams and architecture documentation, see [ARCHITECTURE.md](ARCHITECTURE.md)

### Design Patterns Used

1. **Strategy Pattern**: Different API clients (Guardian, NYTimes)
2. **Builder Pattern**: Model objects construction
3. **Singleton Pattern**: Service beans and configuration
4. **Observer Pattern**: Reactive streams with WebFlux
5. **Factory Pattern**: WebClient configuration
6. **Template Method**: Exception handling structure

## Technology Stack

**Backend:**
- Java 8+
- Spring Boot 2.7.18
- Spring WebFlux
- Spring Security
- Maven

**Frontend:**
- React 18
- Bootstrap 5
- Axios

**Testing & DevOps:**
- JUnit 5 and Mockito for testing
- Cucumber for BDD testing
- Docker and Docker Compose
- Jenkins pipeline
- Swagger/OpenAPI documentation

## Getting Started

### Prerequisites
- Java 8+
- Maven 3.6+
- Node.js 16+ (for frontend)
- Docker (optional)

### API Keys Setup
1. **Guardian API**: Register at https://open-platform.theguardian.com/
2. **NY Times API**: Register at https://developer.nytimes.com/

### Environment Variables
```bash
export GUARDIAN_API_KEY=your_guardian_api_key
export NYTIMES_API_KEY=your_nytimes_api_key
```

### Running Locally

#### Backend
```bash
cd news-aggregator
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm start
```

### Using Docker
```bash
docker-compose up --build
```

## API Documentation

### Base URL
- Local: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### Endpoints

#### Search News
```http
GET /api/v1/news/search?keyword=apple&page=1&pageSize=10&city=london&offlineMode=false
```

**Parameters:**
- `keyword` (required): Search term
- `page` (optional): Page number (default: 1)
- `pageSize` (optional): Results per page (default: 10)
- `city` (optional): City filter
- `offlineMode` (optional): Enable offline mode (default: false)

**Response:**
```json
{
  "articles": [
    {
      "id": "guardian_123",
      "title": "Apple Announces New Features",
      "description": "Apple unveils innovative features...",
      "url": "https://theguardian.com/article",
      "source": "The Guardian",
      "publishedAt": "2024-01-15T10:30:00",
      "imageUrl": "https://image-url.jpg",
      "author": "Tech Reporter",
      "section": "Technology"
    }
  ],
  "searchKeyword": "apple",
  "city": "london",
  "currentPage": 1,
  "totalPages": 5,
  "totalResults": 50,
  "pageSize": 10,
  "previousPage": null,
  "nextPage": 2,
  "executionTimeMs": 245,
  "fromCache": false,
  "offlineMode": false
}
```

#### POST Search
```http
POST /api/v1/news/search
Content-Type: application/json

{
  "keyword": "apple",
  "page": 1,
  "pageSize": 10,
  "city": "london",
  "offlineMode": false
}
```

## Testing

### Unit Tests
```bash
./mvnw test
```

### Integration Tests
```bash
./mvnw verify
```

### Code Coverage
```bash
./mvnw jacoco:report
# View report at target/site/jacoco/index.html
```

### BDD Tests (Cucumber)
```bash
./mvnw test -Dtest=CucumberTestRunner
```

## Docker Deployment

### Build Image
```bash
docker build -t news-aggregator:latest .
```

### Run Container
```bash
docker run -p 8080:8080 \
  -e GUARDIAN_API_KEY=your_key \
  -e NYTIMES_API_KEY=your_key \
  news-aggregator:latest
```

## CI/CD Pipeline

The Jenkins pipeline includes:

1. **Build**: Maven compilation
2. **Test**: Unit and integration tests
3. **Quality**: Code coverage and static analysis
4. **Security**: Docker security scanning
5. **Package**: JAR and Docker image creation
6. **Deploy**: Staging and production deployment

### Pipeline Stages
```groovy
pipeline {
  stages {
    stage('Build') { ... }
    stage('Test') { ... }
    stage('Package') { ... }
    stage('Docker Build') { ... }
    stage('Security Scan') { ... }
    stage('Deploy to Staging') { ... }
    stage('Deploy to Production') { ... }
  }
}
```

## Monitoring & Health Checks

### Health Endpoint
```http
GET /actuator/health
```

### Metrics
```http
GET /actuator/metrics
```

### Application Info
```http
GET /actuator/info
```

## Security Features

- **API Key Encryption**: Sensitive configuration encrypted
- **Input Validation**: Request parameter validation
- **CORS Configuration**: Cross-origin request handling
- **Security Headers**: Standard security headers applied
- **Rate Limiting**: API call throttling (configurable)

## Performance Optimizations

- **Reactive Programming**: Non-blocking I/O with WebFlux
- **Caching**: Response caching for repeated queries
- **Connection Pooling**: Optimized HTTP client configuration
- **Async Processing**: Parallel API calls to multiple sources
- **Memory Management**: Efficient object creation and GC tuning

## Configuration

### Application Properties
```yaml
server:
  port: 8080

spring:
  cache:
    type: simple
    cache-names:
      - newsCache

guardian:
  api:
    key: ${GUARDIAN_API_KEY}
    url: https://content.guardianapis.com

nytimes:
  api:
    key: ${NYTIMES_API_KEY}
```

## Error Handling

The application handles various error scenarios:

- **API Unavailability**: Automatic fallback to offline mode
- **Invalid Parameters**: Validation error responses
- **Rate Limiting**: Graceful degradation
- **Network Timeouts**: Retry mechanisms
- **Malformed Responses**: Error parsing handling

## Scalability Considerations

- **Horizontal Scaling**: Stateless design for multiple instances
- **Load Balancing**: Ready for load balancer integration
- **Database**: Can be extended with persistent storage
- **Caching**: Redis cluster support for distributed caching
- **Message Queues**: Kafka integration for event-driven architecture

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Documentation: [API Documentation](http://localhost:8080/swagger-ui.html)
- Issues: Use the GitHub issues tracker

---

News Aggregator Microservice v1.0.0
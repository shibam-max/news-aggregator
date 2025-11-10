# End-to-End Debugging Guide

## 🔍 Application Debugging Checklist

### 1. Environment Setup Verification

#### Check Java Version
```bash
java -version
# Should show Java 8 or higher
```

#### Check Maven Configuration
```bash
mvn -version
# Verify Maven 3.6+ is installed
```

#### Check Port Availability
```bash
netstat -ano | findstr :8080
# Should be empty or show your application
```

### 2. Application Startup Debugging

#### Enable Debug Logging
```yaml
# application-dev.yml
logging:
  level:
    com.newsaggregator: DEBUG
    org.springframework: DEBUG
    org.hibernate: DEBUG
```

#### Check Application Context Loading
```bash
# Look for these log messages during startup:
# - "Started NewsAggregatorApplication"
# - "Tomcat started on port(s): 8080"
# - "Hibernate Search indexes initialized"
```

#### Verify Database Connection
```bash
# Access H2 Console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:newsdb
# Username: sa
# Password: (empty)
```

### 3. API Endpoint Debugging

#### Health Check
```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

#### API Documentation
```bash
# Swagger UI: http://localhost:8080/swagger-ui.html
# OpenAPI Spec: http://localhost:8080/v3/api-docs
```

#### Test Search Endpoint (Offline Mode)
```bash
curl "http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=true"
```

#### Test Search Endpoint (Online Mode)
```bash
# Set environment variables first:
# GUARDIAN_API_KEY=your_key
# NYTIMES_API_KEY=your_key

curl "http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=false"
```

### 4. Common Issues and Solutions

#### Issue: Port 8080 Already in Use
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID)
taskkill /PID <PID> /F

# Or change application port
server:
  port: 8081
```

#### Issue: API Keys Not Working
```bash
# Check environment variables
echo %GUARDIAN_API_KEY%
echo %NYTIMES_API_KEY%

# Set environment variables
set GUARDIAN_API_KEY=your_guardian_key
set NYTIMES_API_KEY=your_nytimes_key

# Or use application.yml
guardian:
  api:
    key: your_guardian_key
```

#### Issue: Database Connection Failed
```bash
# Check H2 database configuration
spring:
  datasource:
    url: jdbc:h2:mem:newsdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: 
```

#### Issue: Hibernate Search Index Problems
```bash
# Rebuild search index via API
curl -X POST http://localhost:8080/api/v1/admin/rebuild-index

# Check index directory
# Default: ${java.io.tmpdir}/hibernate-search-indexes
```

### 5. Frontend Debugging

#### Check Node.js and npm
```bash
node --version  # Should be 16+
npm --version   # Should be 8+
```

#### Install Dependencies
```bash
cd frontend
npm install
```

#### Start Frontend Development Server
```bash
npm start
# Should open http://localhost:3000
```

#### Check API Connection
```javascript
// Check browser console for API calls
// Network tab should show requests to http://localhost:8080
```

### 6. Docker Debugging

#### Build Docker Image
```bash
docker build -t news-aggregator:latest .
```

#### Run Docker Container
```bash
docker run -p 8080:8080 \
  -e GUARDIAN_API_KEY=your_key \
  -e NYTIMES_API_KEY=your_key \
  news-aggregator:latest
```

#### Check Container Logs
```bash
docker logs <container_id>
```

#### Docker Compose Debugging
```bash
# Start services
docker-compose up --build

# Check service status
docker-compose ps

# View logs
docker-compose logs news-aggregator
```

### 7. Performance Debugging

#### Check JVM Memory Usage
```bash
# Add JVM flags for monitoring
-Xms512m -Xmx1024m -XX:+PrintGCDetails
```

#### Monitor Application Metrics
```bash
# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# Application metrics
curl http://localhost:8080/actuator/metrics
```

#### Check Response Times
```bash
# Use curl with timing
curl -w "@curl-format.txt" -o /dev/null -s "http://localhost:8080/api/v1/news/search?keyword=test&offlineMode=true"

# curl-format.txt content:
#      time_namelookup:  %{time_namelookup}\n
#         time_connect:  %{time_connect}\n
#      time_appconnect:  %{time_appconnect}\n
#     time_pretransfer:  %{time_pretransfer}\n
#        time_redirect:  %{time_redirect}\n
#   time_starttransfer:  %{time_starttransfer}\n
#                      ----------\n
#           time_total:  %{time_total}\n
```

### 8. Circuit Breaker Debugging

#### Check Circuit Breaker Status
```bash
curl http://localhost:8080/actuator/health/circuitBreakers
```

#### Force Circuit Breaker Open (for testing)
```bash
# Make multiple failing requests to trigger circuit breaker
for i in {1..10}; do
  curl "http://localhost:8080/api/v1/news/search?keyword=test&offlineMode=false" &
done
```

#### Monitor Circuit Breaker Events
```bash
# Check application logs for circuit breaker events
grep "Circuit breaker" logs/news-aggregator.log
```

### 9. Database Debugging

#### Check Database Tables
```sql
-- Connect to H2 console and run:
SELECT * FROM news_articles;
SHOW TABLES;
```

#### Check Hibernate Search Index
```bash
# Check if entities are indexed
curl http://localhost:8080/api/v1/search/fuzzy?term=technology
```

#### Monitor Database Queries
```yaml
# Enable SQL logging
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### 10. Testing Debugging

#### Run Unit Tests with Debug
```bash
mvn test -Dtest=EnhancedNewsAggregatorServiceTest -X
```

#### Run Integration Tests
```bash
mvn verify -Dspring.profiles.active=test
```

#### Check Test Coverage
```bash
mvn jacoco:report
# View report: target/site/jacoco/index.html
```

#### Run BDD Tests
```bash
mvn test -Dtest=CucumberTestRunner
```

### 11. Troubleshooting Commands

#### Quick Health Check Script
```bash
#!/bin/bash
echo "=== Application Health Check ==="
curl -s http://localhost:8080/actuator/health | jq .
echo -e "\n=== Test Search API ==="
curl -s "http://localhost:8080/api/v1/news/search?keyword=test&offlineMode=true" | jq .searchKeyword
echo -e "\n=== Check Memory Usage ==="
curl -s http://localhost:8080/actuator/metrics/jvm.memory.used | jq .
```

#### Log Analysis Commands
```bash
# Check for errors in logs
grep -i error logs/news-aggregator.log

# Check for circuit breaker events
grep -i "circuit" logs/news-aggregator.log

# Check API response times
grep "executionTimeMs" logs/news-aggregator.log | tail -10
```

### 12. Production Debugging

#### Check Application Status
```bash
# Health check
curl https://your-domain.com/actuator/health

# Application info
curl https://your-domain.com/actuator/info
```

#### Monitor Resource Usage
```bash
# CPU and Memory
top -p $(pgrep java)

# Disk usage
df -h

# Network connections
netstat -an | grep :8080
```

#### Check Application Logs
```bash
# Tail application logs
tail -f logs/news-aggregator.log

# Search for specific errors
grep -A 5 -B 5 "ERROR" logs/news-aggregator.log
```

## 🚨 Emergency Debugging Checklist

1. **Application Won't Start**
   - Check Java version and JAVA_HOME
   - Verify port availability
   - Check application.yml syntax
   - Review startup logs for errors

2. **API Returns 500 Errors**
   - Check application logs for stack traces
   - Verify database connection
   - Test with offline mode
   - Check API key configuration

3. **Performance Issues**
   - Monitor JVM memory usage
   - Check database query performance
   - Review circuit breaker status
   - Analyze response times

4. **Frontend Not Loading**
   - Check if backend is running
   - Verify CORS configuration
   - Check browser console for errors
   - Test API endpoints directly

5. **Database Issues**
   - Check H2 console connectivity
   - Verify table creation
   - Test Hibernate Search index
   - Review SQL query logs

This debugging guide covers all major components and common issues you might encounter during development and deployment.
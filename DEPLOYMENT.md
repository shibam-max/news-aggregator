# 🚀 News Aggregator Deployment Guide

## Quick Start

### 1. Prerequisites Setup
```bash
# Install Java 17+
java -version

# Install Node.js 16+
node --version
npm --version

# Install Docker (optional)
docker --version
```

### 2. API Keys Configuration
1. **Guardian API Key**: 
   - Visit: https://open-platform.theguardian.com/
   - Register and get your API key
   
2. **NY Times API Key**:
   - Visit: https://developer.nytimes.com/
   - Register and get your API key

### 3. Environment Setup
```bash
# Copy environment template
copy .env.example .env

# Edit .env file with your API keys
GUARDIAN_API_KEY=your_actual_guardian_key
NYTIMES_API_KEY=your_actual_nytimes_key
```

### 4. Local Development

#### Option A: Using Build Script (Windows)
```bash
# Run the automated build script
build-and-run.bat
```

#### Option B: Manual Setup
```bash
# Backend
mvnw.cmd spring-boot:run

# Frontend (in new terminal)
cd frontend
npm install
npm start
```

#### Option C: Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up --build
```

## Production Deployment

### 1. Build Production Artifacts
```bash
# Build backend JAR
mvnw.cmd clean package

# Build frontend
cd frontend
npm run build
```

### 2. Docker Production Deployment
```bash
# Build production image
docker build -t news-aggregator:prod .

# Run production container
docker run -d \
  --name news-aggregator-prod \
  -p 8080:8080 \
  -e GUARDIAN_API_KEY=your_key \
  -e NYTIMES_API_KEY=your_key \
  -e SPRING_PROFILES_ACTIVE=prod \
  news-aggregator:prod
```

### 3. Kubernetes Deployment
```yaml
# k8s-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: news-aggregator
spec:
  replicas: 3
  selector:
    matchLabels:
      app: news-aggregator
  template:
    metadata:
      labels:
        app: news-aggregator
    spec:
      containers:
      - name: news-aggregator
        image: news-aggregator:prod
        ports:
        - containerPort: 8080
        env:
        - name: GUARDIAN_API_KEY
          valueFrom:
            secretKeyRef:
              name: api-keys
              key: guardian-key
        - name: NYTIMES_API_KEY
          valueFrom:
            secretKeyRef:
              name: api-keys
              key: nytimes-key
```

## CI/CD Pipeline Setup

### Jenkins Configuration
1. Create new Pipeline job
2. Point to Jenkinsfile in repository
3. Configure environment variables:
   - `GUARDIAN_API_KEY`
   - `NYTIMES_API_KEY`
   - `DOCKER_REGISTRY`

### GitHub Actions (Alternative)
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-java@v3
      with:
        java-version: '17'
    - run: ./mvnw test
    
  build-and-deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
    - uses: actions/checkout@v3
    - run: docker build -t news-aggregator .
    - run: docker push ${{ secrets.DOCKER_REGISTRY }}/news-aggregator
```

## Monitoring & Health Checks

### Application Health
```bash
# Health check endpoint
curl http://localhost:8080/actuator/health

# Application metrics
curl http://localhost:8080/actuator/metrics

# Application info
curl http://localhost:8080/actuator/info
```

### Log Monitoring
```bash
# View application logs
docker logs news-aggregator-prod

# Follow logs in real-time
docker logs -f news-aggregator-prod
```

## Performance Tuning

### JVM Optimization
```bash
# Production JVM settings
java -Xms512m -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar news-aggregator-1.0.0.jar
```

### Database Configuration (if extended)
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

## Security Considerations

### API Key Management
- Use environment variables
- Never commit keys to version control
- Rotate keys regularly
- Use secret management systems in production

### Network Security
```yaml
# Docker network isolation
networks:
  news-network:
    driver: bridge
    internal: true
```

### HTTPS Configuration
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
```

## Troubleshooting

### Common Issues

1. **API Key Issues**
   ```bash
   # Check environment variables
   echo $GUARDIAN_API_KEY
   echo $NYTIMES_API_KEY
   ```

2. **Port Conflicts**
   ```bash
   # Check port usage
   netstat -an | findstr :8080
   
   # Use different port
   java -Dserver.port=8081 -jar news-aggregator-1.0.0.jar
   ```

3. **Memory Issues**
   ```bash
   # Increase heap size
   java -Xmx4g -jar news-aggregator-1.0.0.jar
   ```

4. **Network Connectivity**
   ```bash
   # Test API connectivity
   curl "https://content.guardianapis.com/search?api-key=test"
   curl "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=test"
   ```

### Debug Mode
```bash
# Enable debug logging
java -Dlogging.level.com.newsaggregator=DEBUG \
     -jar news-aggregator-1.0.0.jar
```

## Scaling Strategies

### Horizontal Scaling
- Deploy multiple instances behind load balancer
- Use shared cache (Redis) for session management
- Database connection pooling

### Vertical Scaling
- Increase JVM heap size
- Optimize garbage collection
- Use faster storage (SSD)

### Caching Strategy
```yaml
spring:
  cache:
    type: redis
    redis:
      host: redis-cluster
      port: 6379
```

## Backup & Recovery

### Configuration Backup
```bash
# Backup configuration files
tar -czf config-backup.tar.gz src/main/resources/
```

### Database Backup (if applicable)
```bash
# PostgreSQL backup
pg_dump newsaggregator > backup.sql
```

## Support & Maintenance

### Regular Maintenance Tasks
1. Update dependencies monthly
2. Rotate API keys quarterly
3. Review logs weekly
4. Performance testing monthly
5. Security scanning weekly

### Monitoring Alerts
- API response time > 5 seconds
- Error rate > 5%
- Memory usage > 80%
- Disk usage > 90%

---

**For additional support, contact: support@newsaggregator.com**
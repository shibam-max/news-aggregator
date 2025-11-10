@echo off
echo ========================================
echo NEWS AGGREGATOR APPLICATION TEST SUITE
echo ========================================

echo.
echo 1. Testing Application Health...
curl -s http://localhost:8080/actuator/health

echo.
echo 2. Testing Offline Search...
curl -s "http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=true" | findstr "searchKeyword"

echo.
echo 3. Testing Validation...
curl -s "http://localhost:8080/api/v1/news/search?keyword=&page=0" | findstr "error"

echo.
echo 4. Testing Hibernate Search...
curl -s "http://localhost:8080/api/v1/search/fulltext?term=technology&maxResults=5" | findstr "title"

echo.
echo 5. Testing Admin Operations...
curl -s -X POST http://localhost:8080/api/v1/admin/rebuild-index | findstr "status"

echo.
echo 6. Testing Metrics...
curl -s http://localhost:8080/actuator/metrics | findstr "jvm"

echo.
echo ========================================
echo TEST SUITE COMPLETED
echo ========================================
echo.
echo Open these URLs in browser:
echo - Application: http://localhost:3000
echo - API Docs: http://localhost:8080/swagger-ui.html
echo - H2 Console: http://localhost:8080/h2-console
echo ========================================
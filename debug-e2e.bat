@echo off
echo ========================================
echo NEWS AGGREGATOR E2E DEBUG WALKTHROUGH
echo ========================================

echo.
echo 1. HEALTH CHECK DEBUG
echo ----------------------------------------
curl -v http://localhost:8080/actuator/health
echo.

echo 2. BASIC API DEBUG (Offline Mode)
echo ----------------------------------------
curl -v "http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=true"
echo.

echo 3. VALIDATION DEBUG (Invalid Request)
echo ----------------------------------------
curl -v "http://localhost:8080/api/v1/news/search?keyword=&page=0"
echo.

echo 4. CIRCUIT BREAKER DEBUG (Online Mode - No API Keys)
echo ----------------------------------------
curl -v "http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=false"
echo.

echo 5. PAGINATION DEBUG
echo ----------------------------------------
curl -v "http://localhost:8080/api/v1/news/search?keyword=business&page=1&pageSize=3&offlineMode=true"
echo.

echo 6. POST REQUEST DEBUG
echo ----------------------------------------
curl -v -X POST http://localhost:8080/api/v1/news/search ^
  -H "Content-Type: application/json" ^
  -d "{\"keyword\":\"sports\",\"page\":1,\"pageSize\":5,\"offlineMode\":true}"
echo.

echo 7. METRICS DEBUG
echo ----------------------------------------
curl -s http://localhost:8080/actuator/metrics/http.server.requests
echo.

echo 8. SWAGGER API DOCS DEBUG
echo ----------------------------------------
curl -s http://localhost:8080/v3/api-docs | findstr "openapi"
echo.

echo ========================================
echo E2E DEBUG COMPLETED
echo ========================================
echo.
echo BROWSER TESTING:
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - H2 Console: http://localhost:8080/h2-console
echo - Application: http://localhost:8080
echo ========================================
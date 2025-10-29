@echo off
echo ========================================
echo News Aggregator Project Verification
echo ========================================

echo.
echo Checking project structure...
if exist "src\main\java\com\newsaggregator\NewsAggregatorApplication.java" (
    echo ✓ Main application class found
) else (
    echo ✗ Main application class missing
)

if exist "src\main\java\com\newsaggregator\controller\NewsController.java" (
    echo ✓ REST controller found
) else (
    echo ✗ REST controller missing
)

if exist "src\main\java\com\newsaggregator\service\NewsAggregatorService.java" (
    echo ✓ Main service found
) else (
    echo ✗ Main service missing
)

if exist "src\main\java\com\newsaggregator\client\GuardianApiClient.java" (
    echo ✓ Guardian API client found
) else (
    echo ✗ Guardian API client missing
)

if exist "src\main\java\com\newsaggregator\client\NYTimesApiClient.java" (
    echo ✓ NY Times API client found
) else (
    echo ✗ NY Times API client missing
)

if exist "src\main\java\com\newsaggregator\util\NewsDeduplicator.java" (
    echo ✓ Custom deduplication logic found
) else (
    echo ✗ Custom deduplication logic missing
)

if exist "src\main\java\com\newsaggregator\util\NewsPaginator.java" (
    echo ✓ Custom pagination logic found
) else (
    echo ✗ Custom pagination logic missing
)

if exist "src\main\java\com\newsaggregator\util\SimpleCache.java" (
    echo ✓ Custom cache implementation found
) else (
    echo ✗ Custom cache implementation missing
)

echo.
echo Checking frontend...
if exist "frontend\src\App.js" (
    echo ✓ React frontend found
) else (
    echo ✗ React frontend missing
)

if exist "src\main\resources\static\index.html" (
    echo ✓ Static HTML frontend found
) else (
    echo ✗ Static HTML frontend missing
)

echo.
echo Checking tests...
if exist "src\test\java\com\newsaggregator\service\NewsAggregatorServiceTest.java" (
    echo ✓ Service tests found
) else (
    echo ✗ Service tests missing
)

if exist "src\test\java\com\newsaggregator\bdd\CucumberTestRunner.java" (
    echo ✓ BDD tests found
) else (
    echo ✗ BDD tests missing
)

if exist "src\test\resources\features\news_search.feature" (
    echo ✓ Cucumber features found
) else (
    echo ✗ Cucumber features missing
)

echo.
echo Checking documentation...
if exist "README.md" (
    echo ✓ README documentation found
) else (
    echo ✗ README documentation missing
)

if exist "ARCHITECTURE.md" (
    echo ✓ Architecture documentation found
) else (
    echo ✗ Architecture documentation missing
)

if exist "API_SPECIFICATION.md" (
    echo ✓ API specification found
) else (
    echo ✗ API specification missing
)

echo.
echo Checking CI/CD...
if exist "Jenkinsfile" (
    echo ✓ Jenkins pipeline found
) else (
    echo ✗ Jenkins pipeline missing
)

if exist "Dockerfile" (
    echo ✓ Docker configuration found
) else (
    echo ✗ Docker configuration missing
)

if exist "docker-compose.yml" (
    echo ✓ Docker Compose found
) else (
    echo ✗ Docker Compose missing
)

echo.
echo ========================================
echo Project verification complete!
echo ========================================
echo.
echo To run the project:
echo 1. Set API keys: GUARDIAN_API_KEY and NYTIMES_API_KEY
echo 2. Run: build-and-run.bat
echo 3. Access: http://localhost:8080 (API + Static UI)
echo 4. Access: http://localhost:3000 (React UI)
echo 5. Swagger: http://localhost:8080/swagger-ui.html
echo.
pause
@echo off
echo ========================================
echo Building News Aggregator Microservice
echo ========================================

REM Set API keys (replace with your actual keys)
set GUARDIAN_API_KEY=your_guardian_api_key_here
set NYTIMES_API_KEY=your_nytimes_api_key_here

echo.
echo Step 1: Building backend...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Backend build failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Building frontend...
cd frontend
call npm install
call npm run build
cd ..

echo.
echo Step 3: Starting application...
echo Backend will be available at: http://localhost:8080
echo Frontend will be available at: http://localhost:3000
echo Swagger UI: http://localhost:8080/swagger-ui.html
echo.

start "News Aggregator Backend" java -jar target/news-aggregator-1.0.0.jar
timeout /t 10 /nobreak

cd frontend
start "News Aggregator Frontend" npm start

echo.
echo ========================================
echo News Aggregator is starting up!
echo ========================================
echo.
echo API Endpoints:
echo - GET /api/v1/news/search?keyword=apple
echo - POST /api/v1/news/search
echo.
echo Press any key to exit...
pause
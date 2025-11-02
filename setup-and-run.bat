@echo off
echo ========================================
echo News Aggregator Setup and Run Script
echo ========================================
echo.

echo This script will help you set up and run the News Aggregator with real API keys.
echo.

echo Step 1: API Keys Setup
echo ----------------------
echo You need API keys from Guardian and NY Times to get real news data.
echo.
echo Guardian API: https://open-platform.theguardian.com/access/
echo NY Times API: https://developer.nytimes.com/get-started
echo.
echo Press any key when you have your API keys ready...
pause >nul
echo.

:input_keys
echo Step 2: Enter Your API Keys
echo ---------------------------
set /p GUARDIAN_KEY="Enter your Guardian API key: "
set /p NYTIMES_KEY="Enter your NY Times API key: "
echo.

if "%GUARDIAN_KEY%"=="" (
    echo Error: Guardian API key cannot be empty!
    goto input_keys
)

if "%NYTIMES_KEY%"=="" (
    echo Error: NY Times API key cannot be empty!
    goto input_keys
)

echo Step 3: Starting the Application
echo --------------------------------
echo Setting environment variables...
set GUARDIAN_API_KEY=%GUARDIAN_KEY%
set NYTIMES_API_KEY=%NYTIMES_KEY%

echo Building and starting the application...
echo This may take a few minutes on first run...
echo.

call mvnw clean spring-boot:run

if errorlevel 1 (
    echo.
    echo ========================================
    echo Build failed! Please check the errors above.
    echo ========================================
    pause
    exit /b 1
)

echo.
echo ========================================
echo News Aggregator is now running!
echo ========================================
echo.
echo Access your application at:
echo - Main API: http://localhost:8080/api/v1/news/search?keyword=apple
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo - Health Check: http://localhost:8080/actuator/health
echo.
echo Test with real news:
echo http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=false
echo.
echo Press any key to exit...
pause >nul


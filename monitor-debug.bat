@echo off
echo ========================================
echo DEBUG LOG MONITORING COMMANDS
echo ========================================

echo.
echo 1. Monitor debug logs in real-time:
echo tail -f logs/news-aggregator.log | findstr DEBUG
echo.

echo 2. Filter specific debug points:
echo tail -f logs/news-aggregator.log | findstr "DEBUG-1\|DEBUG-4\|DEBUG-17"
echo.

echo 3. Monitor all application logs:
echo tail -f logs/news-aggregator.log
echo.

echo 4. Test requests to trigger debug flow:
echo curl "http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=true"
echo curl "http://localhost:8080/api/v1/news/search?keyword=business&offlineMode=true"
echo curl "http://localhost:8080/api/v1/news/search?keyword=apple&offlineMode=true"
echo.

echo ========================================
echo DEBUG FLOW VERIFICATION
echo ========================================
echo.
echo Look for these debug messages in console:
echo [DEBUG-1] Request received
echo [DEBUG-4] Service entry  
echo [DEBUG-17] OfflineDataService
echo [DEBUG-3] Service response
echo.
echo ========================================
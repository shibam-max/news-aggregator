@echo off
echo ========================================
echo DEBUG FLOW TRACING GUIDE
echo ========================================

echo.
echo STEP 1: Start application with debug profile
echo ----------------------------------------
echo Command: mvn spring-boot:run -Dspring-boot.run.profiles=dev
echo.
echo Watch for these debug points in logs:
echo [DEBUG-1] Request received
echo [DEBUG-2] Request object created  
echo [DEBUG-3] Service response
echo [DEBUG-4] Service method entry
echo [DEBUG-5] Cache key generated
echo [DEBUG-6/7] Cache hit/miss
echo [DEBUG-8/9] Offline/Online mode
echo [DEBUG-10-16] API calls and aggregation
echo [DEBUG-17-19] Offline data filtering
echo.

echo STEP 2: Test with debug tracing
echo ----------------------------------------
echo curl "http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=true"
echo.

echo STEP 3: Monitor logs in real-time
echo ----------------------------------------
echo tail -f logs/news-aggregator.log | findstr DEBUG
echo.

echo STEP 4: IDE Debugging Setup
echo ----------------------------------------
echo 1. Set breakpoints at:
echo    - NewsController.searchNews() line 45
echo    - NewsAggregatorService.searchNews() line 25
echo    - OfflineDataService.getOfflineArticles() line 18
echo.
echo 2. Debug mode: Run -> Debug 'NewsAggregatorApplication'
echo.

echo ========================================
echo DEBUG POINTS FLOW:
echo ========================================
echo 1. HTTP Request -> Controller [DEBUG-1,2,3]
echo 2. Service Entry -> Cache Check [DEBUG-4,5,6,7]  
echo 3. Mode Decision -> API/Offline [DEBUG-8,9]
echo 4. API Calls -> Aggregation [DEBUG-10-16]
echo 5. Offline Data -> Filtering [DEBUG-17-19]
echo 6. Response -> Controller -> HTTP Response
echo ========================================
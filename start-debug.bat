@echo off
echo ========================================
echo STARTING APPLICATION IN DEBUG MODE
echo ========================================

echo.
echo Starting Spring Boot application with debug enabled...
echo Debug port: 5005
echo Profile: dev
echo.

mvn spring-boot:run ^
  -Dspring-boot.run.profiles=dev ^
  -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

echo.
echo ========================================
echo APPLICATION STARTED IN DEBUG MODE
echo ========================================
echo.
echo Connect your IDE debugger to:
echo Host: localhost
echo Port: 5005
echo.
echo Test the application:
echo curl "http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=true"
echo ========================================
@echo off
echo ========================================
echo    Starting API Gateway Microservices
echo ========================================
echo.

echo 1. Starting services...
call NEW_START.bat

echo.
echo 2. Testing MongoDB connection...
node mongodb-connection-test.js

echo.
echo 3. Checking all services...
node check-all-services.js

echo.
echo ========================================
echo       System is ready to use!
echo ========================================
echo.
echo Open http://localhost:3002/login.html in your browser
echo.
echo Login credentials:
echo - Admin: admin@example.com / admin123
echo - User:  user@example.com / user123
echo.

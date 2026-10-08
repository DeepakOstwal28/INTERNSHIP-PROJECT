@echo off
title STREAMFLOW Media Streaming Platform
echo ========================================================
echo   Starting STREAMFLOW Enterprise Media Platform (Spring Boot)
echo ========================================================
echo.
echo Application URL: http://localhost:8080
echo H2 SQL Database Console: http://localhost:8080/h2-console
echo.
echo Demo Credentials:
echo   - Admin User: username: admin  ^| password: admin123
echo   - Regular User: username: user ^| password: user123
echo.
echo ========================================================
mvn spring-boot:run
pause

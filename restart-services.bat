@echo off
echo ========================================
echo    Restarting API Gateway Services
echo ========================================
echo.

echo Killing existing Node.js processes...
taskkill /IM node.exe /F 2>nul
timeout /t 2 /nobreak > nul

echo Starting Auth Service on port 3001...
start "Auth Service" cmd /k "cd /d %~dp0auth-service && node server.js"
timeout /t 5 /nobreak > nul

echo Starting API Gateway on port 3000...
start "API Gateway" cmd /k "cd /d %~dp0api-gateway && node server.js"
timeout /t 2 /nobreak > nul

echo Starting Frontend on port 3002...
start "Frontend" cmd /k "cd /d %~dp0frontend && node server.js"
timeout /t 2 /nobreak > nul

echo Starting Movies Service on port 3003...
start "Movies Service" cmd /k "cd /d %~dp0movies-service && node server.js"
timeout /t 1 /nobreak > nul

echo Starting Music Service on port 3004...
start "Music Service" cmd /k "cd /d %~dp0music-service && node server.js"
timeout /t 1 /nobreak > nul

echo Starting Anime Service on port 3005...
start "Anime Service" cmd /k "cd /d %~dp0anime-service && node server.js"
timeout /t 1 /nobreak > nul

echo Starting Videos Service on port 3006...
start "Videos Service" cmd /k "cd /d %~dp0videos-service && node server.js"
timeout /t 1 /nobreak > nul

echo Starting Notification Service on port 3007...
start "Notification Service" cmd /k "cd /d %~dp0notification-service && node server.js"
timeout /t 1 /nobreak > nul

echo.
echo ========================================
echo      All Services Started! 🚀
echo ========================================
echo.
echo 🔐 Auth Service:          http://localhost:3001
echo 🌐 API Gateway:           http://localhost:3000
echo 🖥️ Frontend:              http://localhost:3002
echo 🎬 Movies Service:        http://localhost:3003
echo 🎵 Music Service:         http://localhost:3004
echo 📺 Anime Service:         http://localhost:3005
echo 📹 Videos Service:        http://localhost:3006
echo 🔔 Notification Service:  http://localhost:3007
echo.
echo Press any key to exit...
pause > nul

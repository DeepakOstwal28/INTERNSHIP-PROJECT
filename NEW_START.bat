@echo off
echo Starting API Gateway Microservices Platform...

start cmd /k "cd auth-service && npm start"
timeout /t 2 > nul

start cmd /k "cd api-gateway && npm start"
timeout /t 2 > nul

start cmd /k "cd frontend && npm start"
timeout /t 2 > nul

start cmd /k "cd movies-service && npm start"
timeout /t 2 > nul

start cmd /k "cd music-service && npm start"
timeout /t 2 > nul

start cmd /k "cd anime-service && npm start"
timeout /t 2 > nul

start cmd /k "cd videos-service && npm start"
timeout /t 2 > nul

start cmd /k "cd notification-service && npm start"
timeout /t 2 > nul

start cmd /k "cd load-balancer && npm start"

echo All services started!
echo.
echo Frontend: http://localhost:3002
echo API Gateway: http://localhost:3000
echo Auth Service: http://localhost:3001
echo.

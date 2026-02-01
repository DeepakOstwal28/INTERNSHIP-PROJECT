@echo off
echo Building and deploying API Gateway Microservices using Docker...

docker-compose build
docker-compose up -d

echo.
echo Docker containers started! Services available at:
echo.
echo Frontend: http://localhost:3002
echo API Gateway: http://localhost:3000
echo Auth Service: http://localhost:3001
echo Movies Service: http://localhost:3003
echo Music Service: http://localhost:3004
echo Anime Service: http://localhost:3005
echo Videos Service: http://localhost:3006
echo Notification Service: http://localhost:3007
echo Load Balancer: http://localhost:3100
echo.

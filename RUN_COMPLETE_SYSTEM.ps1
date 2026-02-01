# API Gateway Complete System Startup Script
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   STARTING API GATEWAY COMPLETE SYSTEM    " -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Function to check if port is available
function Test-Port {
    param($Port)
    $connection = New-Object System.Net.Sockets.TcpClient
    try {
        $connection.Connect("localhost", $Port)
        $connection.Close()
        return $true
    } catch {
        return $false
    }
}

# Kill existing Node processes
Write-Host "Cleaning up existing processes..." -ForegroundColor Yellow
Get-Process node -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

Write-Host "Installing dependencies..." -ForegroundColor Yellow
Write-Host ""

# Navigate to each service and install dependencies
$services = @(
    @{Name="Auth Service"; Path="auth-service"},
    @{Name="API Gateway"; Path="api-gateway"},
    @{Name="Frontend"; Path="frontend"},
    @{Name="Movies Service"; Path="movies-service"},
    @{Name="Music Service"; Path="music-service"},
    @{Name="Anime Service"; Path="anime-service"},
    @{Name="Videos Service"; Path="videos-service"},
    @{Name="Notification Service"; Path="notification-service"},
    @{Name="Load Balancer"; Path="load-balancer"}
)

foreach ($service in $services) {
    if (Test-Path $service.Path) {
        Write-Host "Installing $($service.Name) dependencies..." -ForegroundColor White
        Push-Location $service.Path
        npm install --silent 2>$null
        Pop-Location
    }
}

Write-Host ""
Write-Host "Starting all services..." -ForegroundColor Green
Write-Host ""

# Start Auth Service
Write-Host "1. Starting Auth Service (Port 3001)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd auth-service; Write-Host 'AUTH SERVICE STARTED' -ForegroundColor Green; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 3

# Start Frontend Service
Write-Host "2. Starting Frontend Service (Port 3002)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend; Write-Host 'FRONTEND SERVICE STARTED' -ForegroundColor Blue; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 3

# Start Movies Service
Write-Host "3. Starting Movies Service (Port 3003)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd movies-service; Write-Host 'MOVIES SERVICE STARTED' -ForegroundColor Magenta; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 2

# Start Music Service
Write-Host "4. Starting Music Service (Port 3004)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd music-service; Write-Host 'MUSIC SERVICE STARTED' -ForegroundColor Yellow; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 2

# Start Anime Service
Write-Host "5. Starting Anime Service (Port 3005)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd anime-service; Write-Host 'ANIME SERVICE STARTED' -ForegroundColor Green; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 2

# Start Videos Service
Write-Host "6. Starting Videos Service (Port 3006)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd videos-service; Write-Host 'VIDEOS SERVICE STARTED' -ForegroundColor Blue; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 2

# Start Notification Service
Write-Host "7. Starting Notification Service (Port 3007)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd notification-service; Write-Host 'NOTIFICATION SERVICE STARTED' -ForegroundColor Magenta; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 2

# Start Load Balancer
Write-Host "8. Starting Load Balancer (Port 3100)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd load-balancer; Write-Host 'LOAD BALANCER STARTED' -ForegroundColor Yellow; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 2

# Start API Gateway
Write-Host "9. Starting API Gateway (Port 3000)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd api-gateway; Write-Host 'API GATEWAY STARTED' -ForegroundColor Green; npm start" -WindowStyle Minimized
Start-Sleep -Seconds 5

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "     ALL SERVICES STARTED SUCCESSFULLY!    " -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""

# Check service status
Write-Host "Verifying services..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

$serviceCheck = @(
    @{Name="API Gateway"; Port=3000; URL="http://localhost:3000/health"},
    @{Name="Auth Service"; Port=3001; URL="http://localhost:3001/health"},
    @{Name="Frontend"; Port=3002; URL="http://localhost:3002"},
    @{Name="Movies Service"; Port=3003; URL="http://localhost:3003/health"},
    @{Name="Music Service"; Port=3004; URL="http://localhost:3004/health"},
    @{Name="Anime Service"; Port=3005; URL="http://localhost:3005/health"},
    @{Name="Videos Service"; Port=3006; URL="http://localhost:3006/health"},
    @{Name="Notification Service"; Port=3007; URL="http://localhost:3007/health"},
    @{Name="Load Balancer"; Port=3100; URL="http://localhost:3100"}
)

foreach ($service in $serviceCheck) {
    if (Test-Port $service.Port) {
        Write-Host "✅ $($service.Name) is running on port $($service.Port)" -ForegroundColor Green
    } else {
        Write-Host "⚠️ $($service.Name) is not responding on port $($service.Port)" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "           ACCESS URLS                      " -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "🏠 Main Application:    " -NoNewline; Write-Host "http://localhost:3002/login.html" -ForegroundColor Yellow
Write-Host "👨‍💼 Admin Panel:        " -NoNewline; Write-Host "http://localhost:3002/admin-new.html" -ForegroundColor Yellow
Write-Host "👤 User Dashboard:      " -NoNewline; Write-Host "http://localhost:3002/dashboard-professional.html" -ForegroundColor Yellow
Write-Host "📊 API Health:          " -NoNewline; Write-Host "http://localhost:3000/health" -ForegroundColor Yellow
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "           TEST CREDENTIALS                 " -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Admin Account:" -ForegroundColor White
Write-Host "  📧 Email: " -NoNewline; Write-Host "admin@example.com" -ForegroundColor Green
Write-Host "  🔑 Password: " -NoNewline; Write-Host "admin123" -ForegroundColor Green
Write-Host ""
Write-Host "User Account:" -ForegroundColor White
Write-Host "  📧 Email: " -NoNewline; Write-Host "user@example.com" -ForegroundColor Green
Write-Host "  🔑 Password: " -NoNewline; Write-Host "user123" -ForegroundColor Green
Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Opening Login Page in browser..." -ForegroundColor Yellow
Start-Sleep -Seconds 2
Start-Process "http://localhost:3002/login.html"

Write-Host ""
Write-Host "✨ System is ready for use!" -ForegroundColor Green
Write-Host ""
Write-Host "To stop all services, close the PowerShell windows or press Ctrl+C" -ForegroundColor Gray
Write-Host ""

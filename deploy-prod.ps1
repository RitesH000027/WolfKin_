# E-Commerce Production Deployment Script
Write-Host "Deploying E-Commerce to Production..." -ForegroundColor Green

# Confirm production deployment
$confirm = Read-Host "Are you sure you want to deploy to production? (y/N)"
if ($confirm -ne "y" -and $confirm -ne "Y") {
    Write-Host "Deployment cancelled." -ForegroundColor Yellow
    exit
}

# Check if production environment file exists
if (-not (Test-Path ".env.prod")) {
    Write-Host "Production environment file (.env.prod) not found!" -ForegroundColor Red
    Write-Host "Please create .env.prod with production configuration." -ForegroundColor Red
    exit 1
}

# Stop existing production containers
Write-Host "Stopping existing production containers..." -ForegroundColor Yellow
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod down

# Build production images
Write-Host "Building production images..." -ForegroundColor Yellow
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod build --no-cache

# Start production environment
Write-Host "Starting production environment..." -ForegroundColor Yellow
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod up -d

# Wait for services
Write-Host "Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Health check
Write-Host "Performing health checks..." -ForegroundColor Yellow
try {
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 10
    if ($healthCheck.status -eq "UP") {
        Write-Host "Backend health check passed ✓" -ForegroundColor Green
    } else {
        Write-Host "Backend health check failed!" -ForegroundColor Red
    }
} catch {
    Write-Host "Backend health check failed: $_" -ForegroundColor Red
}

# Show final status
Write-Host ""
Write-Host "Production Deployment Status:" -ForegroundColor Cyan
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod ps

Write-Host ""
Write-Host "Production deployment completed!" -ForegroundColor Green
Write-Host "Application: http://localhost" -ForegroundColor Cyan
Write-Host "API Health: http://localhost:8080/actuator/health" -ForegroundColor Cyan
Read-Host "Press Enter to exit"
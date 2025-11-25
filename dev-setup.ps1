# E-Commerce Development Environment Setup
Write-Host "Starting E-Commerce Development Environment..." -ForegroundColor Green
Write-Host ""

# Check if Docker is running
try {
    docker info | Out-Null
    Write-Host "Docker is running ✓" -ForegroundColor Green
} catch {
    Write-Host "Docker is not running. Please start Docker Desktop first." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Copy environment file if it doesn't exist
if (-not (Test-Path ".env")) {
    Write-Host "Creating .env file from template..." -ForegroundColor Yellow
    Copy-Item ".env.docker" ".env"
}

# Build and start development environment
Write-Host "Building Docker images..." -ForegroundColor Yellow
docker-compose -f docker-compose.yml -f docker-compose.dev.yml build --no-cache

Write-Host ""
Write-Host "Starting services..." -ForegroundColor Yellow
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Wait for services to be ready
Write-Host ""
Write-Host "Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# Show status
Write-Host ""
Write-Host "Service Status:" -ForegroundColor Cyan
docker-compose -f docker-compose.yml -f docker-compose.dev.yml ps

Write-Host ""
Write-Host "Development environment is ready!" -ForegroundColor Green
Write-Host ""
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "Backend API: http://localhost:8080/api" -ForegroundColor Cyan
Write-Host "Backend Health: http://localhost:8080/actuator/health" -ForegroundColor Cyan
Write-Host ""
Write-Host "Useful commands:" -ForegroundColor Yellow
Write-Host "  View logs: docker-compose logs -f [service-name]"
Write-Host "  Stop all: docker-compose -f docker-compose.yml -f docker-compose.dev.yml down"
Write-Host "  Restart: docker-compose -f docker-compose.yml -f docker-compose.dev.yml restart [service-name]"
Write-Host ""
Read-Host "Press Enter to continue"
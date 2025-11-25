# E-Commerce Logs Viewer
Write-Host "E-Commerce Development Logs Viewer" -ForegroundColor Green
Write-Host ""

# Check if containers are running
$containers = docker-compose -f docker-compose.yml -f docker-compose.dev.yml ps -q
if (-not $containers) {
    Write-Host "No containers are running. Start the development environment first." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit
}

Write-Host "Available services:"
Write-Host "1. Backend (Spring Boot)"
Write-Host "2. Frontend (Nginx)"
Write-Host "3. PostgreSQL Database"
Write-Host "4. Redis Cache"
Write-Host "5. RabbitMQ"
Write-Host "6. All services"
Write-Host "7. Follow all logs (live)"
Write-Host ""

$choice = Read-Host "Select service to view logs (1-7)"

switch ($choice) {
    "1" { 
        Write-Host "Showing Backend logs..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs backend
    }
    "2" { 
        Write-Host "Showing Frontend logs..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs frontend
    }
    "3" { 
        Write-Host "Showing Database logs..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs postgres
    }
    "4" { 
        Write-Host "Showing Redis logs..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs redis
    }
    "5" { 
        Write-Host "Showing RabbitMQ logs..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs rabbitmq
    }
    "6" { 
        Write-Host "Showing all logs..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs
    }
    "7" { 
        Write-Host "Following all logs (Press Ctrl+C to stop)..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs -f
    }
    default { 
        Write-Host "Invalid choice. Showing all logs..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs
    }
}

Read-Host "Press Enter to exit"
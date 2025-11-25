# E-Commerce Environment Cleanup Script
Write-Host "Cleaning up E-Commerce Development Environment..." -ForegroundColor Yellow

# Stop and remove containers
Write-Host "Stopping containers..." -ForegroundColor Yellow
docker-compose -f docker-compose.yml -f docker-compose.dev.yml down

# Remove images (optional)
$removeImages = Read-Host "Remove Docker images as well? (y/N)"
if ($removeImages -eq "y" -or $removeImages -eq "Y") {
    Write-Host "Removing Docker images..." -ForegroundColor Yellow
    docker rmi ecom-backend:latest -f 2>$null
    docker rmi ecom-frontend:latest -f 2>$null
}

# Clean up volumes (optional)
$removeVolumes = Read-Host "Remove Docker volumes (THIS WILL DELETE DATABASE DATA)? (y/N)"
if ($removeVolumes -eq "y" -or $removeVolumes -eq "Y") {
    Write-Host "Removing Docker volumes..." -ForegroundColor Red
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml down -v
    docker volume prune -f
}

# Clean up build artifacts
Write-Host "Cleaning build artifacts..." -ForegroundColor Yellow
if (Test-Path "backend/target") {
    Remove-Item "backend/target" -Recurse -Force
}
if (Test-Path "frontend/dist") {
    Remove-Item "frontend/dist" -Recurse -Force
}
if (Test-Path "frontend/node_modules") {
    $removeNodeModules = Read-Host "Remove node_modules? (y/N)"
    if ($removeNodeModules -eq "y" -or $removeNodeModules -eq "Y") {
        Remove-Item "frontend/node_modules" -Recurse -Force
    }
}

Write-Host "Cleanup completed!" -ForegroundColor Green
Read-Host "Press Enter to exit"
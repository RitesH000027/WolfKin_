@echo off
echo Starting E-Commerce Development Environment...
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

REM Copy environment file if it doesn't exist
if not exist ".env" (
    echo Creating .env file from template...
    copy ".env.docker" ".env"
)

REM Build and start development environment
echo Building Docker images...
docker-compose -f docker-compose.yml -f docker-compose.dev.yml build --no-cache

echo.
echo Starting services...
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

REM Wait for services to be ready
echo.
echo Waiting for services to start...
timeout /t 10 /nobreak >nul

REM Show status
echo.
echo Service Status:
docker-compose -f docker-compose.yml -f docker-compose.dev.yml ps

echo.
echo Development environment is ready!
echo.
echo Frontend: http://localhost:3000
echo Backend API: http://localhost:8080/api
echo Backend Health: http://localhost:8080/actuator/health
echo.
echo To view logs: docker-compose logs -f [service-name]
echo To stop: docker-compose -f docker-compose.yml -f docker-compose.dev.yml down
echo.
pause
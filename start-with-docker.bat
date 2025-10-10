@echo off
echo Starting Clipers Backend with Docker Compose...

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running. Please start Docker Desktop.
    pause
    exit /b 1
)

REM Navigate to the backend directory
cd /d "%~dp0"

REM Stop any existing containers
echo Stopping existing containers...
docker-compose down

REM Build and start services
echo Building and starting services...
docker-compose up --build -d

if %errorlevel% neq 0 (
    echo ERROR: Failed to start Docker containers.
    pause
    exit /b 1
)

echo.
echo Services started successfully!
echo Backend: http://localhost:8080
echo PostgreSQL: localhost:5432
echo Redis: localhost:6379
echo.
echo To view logs: docker-compose logs -f
echo To stop services: docker-compose down
echo.

pause
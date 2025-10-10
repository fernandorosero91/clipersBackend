@echo off
echo Starting Clipers Backend with PostgreSQL configuration...

REM Check if PostgreSQL service is running
net start | findstr "postgresql"
if %errorlevel% neq 0 (
    echo PostgreSQL service is not running. Starting it...
    net start postgresql
    if %errorlevel% neq 0 (
        echo ERROR: Failed to start PostgreSQL service. Please start it manually.
        pause
        exit /b 1
    )
)

REM Navigate to the backend directory
cd /d "%~dp0"

REM Build and run the Spring Boot application with PostgreSQL profile
echo Building application with PostgreSQL profile...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo ERROR: Failed to build the application.
    pause
    exit /b 1
)

echo Starting Spring Boot application with PostgreSQL...
java -jar target/clipers-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=local-postgres

pause
@echo off
echo Testing PostgreSQL connection for Clipers project...

REM Check if PostgreSQL service is running
net start | findstr "postgresql"
if %errorlevel% neq 0 (
    echo PostgreSQL service is not running. Starting it...
    net start postgresql
    if %errorlevel% neq 0 (
        echo Failed to start PostgreSQL service. Please start it manually.
        pause
        exit /b 1
    )
)

REM Test database connection using psql
echo Testing connection to database clipers_db...
psql -U clipers_user -d clipers_db -c "SELECT version();"

if %errorlevel% equ 0 (
    echo SUCCESS: Connection to PostgreSQL database established!
    echo Database: clipers_db
    echo User: clipers_user
    echo You can now run your Spring Boot application with PostgreSQL.
) else (
    echo ERROR: Failed to connect to PostgreSQL database.
    echo Please check:
    echo 1. PostgreSQL service is running
    echo 2. Database clipers_db exists
    echo 3. User clipers_user exists with correct password
    echo 4. Connection parameters are correct
)

pause
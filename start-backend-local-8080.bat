@echo off
echo ========================================
echo   INICIANDO CLIPERS BACKEND LOCAL 8080
echo ========================================
echo.

echo Verificando servicios locales...
echo.

REM Verificar PostgreSQL
echo 1. Verificando PostgreSQL en localhost:5432...
net start | findstr "postgresql"
if %errorlevel% equ 0 (
    echo PostgreSQL service is running.
) else (
    echo WARNING: PostgreSQL service is not running. Please start it manually.
    echo You can start it with: net start postgresql
)

echo.
REM Verificar Redis
echo 2. Verificando Redis en localhost:6379...
redis-cli ping >nul 2>&1
if %errorlevel% equ 0 (
    echo Redis is running and responding.
) else (
    echo WARNING: Redis is not running or not responding on localhost:6379.
    echo Please start Redis manually or install it if you don't have it.
)

echo.
echo Iniciando aplicacion Spring Boot en puerto 8080...
echo.

REM Navegar al directorio del backend
cd /d "%~dp0"

REM Ejecutar con perfil local-8080 específico
echo Ejecutando con perfil local-8080 (PostgreSQL + Redis)...
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local-8080

echo.
echo ========================================
echo   BACKEND INICIADO EN http://localhost:8080
echo ========================================
echo.
echo Endpoints disponibles:
echo - API: http://localhost:8080
echo - Health Check: http://localhost:8080/actuator/health
echo - Swagger UI: http://localhost:8080/swagger-ui.html
echo.
pause
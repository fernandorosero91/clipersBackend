@echo off
echo ========================================
echo   INICIANDO CLIPERS BACKEND
echo ========================================
echo.

echo Verificando servicios Docker...
docker-compose ps

echo.
echo Iniciando aplicacion Spring Boot...
echo.

.\mvnw.cmd clean spring-boot:run

pause
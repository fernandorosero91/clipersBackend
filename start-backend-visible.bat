@echo off
echo ========================================
echo   INICIANDO CLIPERS BACKEND
echo ========================================
echo.

echo Verificando servicios Docker...
docker-compose ps

echo.
echo Iniciando Spring Boot con logs visibles...
echo Presiona Ctrl+C para detener
echo.

.\mvnw.cmd spring-boot:run

pause



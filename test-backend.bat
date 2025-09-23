@echo off
echo ========================================
echo   PROBANDO BACKEND CLIPERS
echo ========================================
echo.

echo 1. Verificando puerto 8080...
netstat -an | findstr :8080
echo.

echo 2. Verificando procesos Java...
tasklist /fi "imagename eq java.exe" 2>nul
echo.

echo 3. Probando conexion HTTP...
curl -s -o nul -w "Status: %%{http_code}\n" http://localhost:8080 2>nul || echo "No se pudo conectar"
echo.

echo 4. Probando endpoint de salud...
curl -s -o nul -w "Health Status: %%{http_code}\n" http://localhost:8080/actuator/health 2>nul || echo "Endpoint de salud no disponible"
echo.

echo ========================================
pause

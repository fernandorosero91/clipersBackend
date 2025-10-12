@echo off
echo ========================================
echo   PROBANDO BACKEND CLIPERS 8080
echo ========================================
echo.

echo 1. Verificando puerto 8080...
netstat -an | findstr :8080
if %errorlevel% equ 0 (
    echo Puerto 8080 está en uso.
) else (
    echo Puerto 8080 no está en uso.
)

echo.
echo 2. Verificando procesos Java...
tasklist /fi "imagename eq java.exe" 2>nul | find clipers
if %errorlevel% equ 0 (
    echo Proceso Java de Clipers encontrado.
) else (
    echo Proceso Java de Clipers no encontrado.
)

echo.
echo 3. Probando conexion HTTP...
curl -s -o nul -w "Status: %%{http_code}\n" http://localhost:8080 2>nul || echo "No se pudo conectar a http://localhost:8080"

echo.
echo 4. Probando endpoint de salud...
curl -s -o nul -w "Health Status: %%{http_code}\n" http://localhost:8080/actuator/health 2>nul || echo "Endpoint de salud no disponible"

echo.
echo 5. Probando endpoint de API...
curl -s -o nul -w "API Status: %%{http_code}\n" http://localhost:8080/api/auth/me 2>nul || echo "Endpoint de API no disponible"

echo.
echo 6. Verificando logs recientes...
if exist "logs\clipers-backend.log" (
    echo Ultimas 5 lineas del log:
    tail -n 5 "logs\clipers-backend.log" 2>nul || echo "No se pudo leer el archivo de log"
) else (
    echo Archivo de log no encontrado en logs\clipers-backend.log
)

echo.
echo ========================================
echo   FIN DE PRUEBAS
echo ========================================
pause
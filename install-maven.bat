@echo off
echo Instalando Maven usando Chocolatey...
echo.

echo Verificando si Chocolatey esta instalado...
choco -v >nul 2>&1
if errorlevel 1 (
    echo Chocolatey no esta instalado. Instalando Chocolatey primero...
    echo.
    powershell -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))"
    echo.
    echo Chocolatey instalado. Reinicia PowerShell y ejecuta este script nuevamente.
    pause
    exit /b 0
)

echo Chocolatey detectado. Instalando Maven...
choco install maven -y

echo.
echo Maven instalado. Verificando instalacion...
mvn -version

echo.
echo ========================================
echo Maven instalado exitosamente!
echo ========================================
echo.
echo Ahora puedes ejecutar:
echo   mvn spring-boot:run
echo.
pause

@echo off
echo Iniciando Backend de Clipers (Solo Maven)...
echo.

echo NOTA: Esta opcion inicia solo la aplicacion Spring Boot.
echo Necesitaras PostgreSQL, Redis y RabbitMQ ejecutandose por separado.
echo.

echo Verificando Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven no esta instalado o no esta en el PATH.
    echo Por favor instala Maven y ejecuta este script nuevamente.
    pause
    exit /b 1
)

echo Maven esta disponible.
echo.

echo Compilando aplicacion...
mvn clean compile -DskipTests

echo.
echo Iniciando aplicacion Spring Boot...
echo ========================================
echo Backend de Clipers iniciando...
echo API estara disponible en: http://localhost:8080
echo ========================================
echo.

mvn spring-boot:run

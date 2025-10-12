#!/bin/bash

# Script para levantar los servicios de Clipers en WSL con Docker
# Este script debe ejecutarse desde el directorio clipersBackend

echo "🚀 Iniciando servicios de Clipers en WSL con Docker..."

# Verificar si Docker está corriendo
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker no está corriendo. Por favor, inicia Docker Desktop."
    exit 1
fi

# Verificar si estamos en el directorio correcto
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ No se encontró docker-compose.yml en el directorio actual."
    echo "Por favor, ejecuta este script desde el directorio clipersBackend."
    exit 1
fi

# Crear archivo .env si no existe
if [ ! -f ".env" ]; then
    echo "📝 Creando archivo .env con configuración por defecto..."
    cat > .env << EOF
# Contraseña segura para PostgreSQL
POSTGRES_PASSWORD=ClipersSecurePass123!

# Clave secreta para JWT
JWT_SECRET=mySuperSecretKey123456789012345678901234567890123456789012345678901234567890
EOF
fi

# Levantar los servicios
echo "🐳 Levantando contenedores..."
docker-compose up -d --build

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios se inicien..."
sleep 10

# Verificar el estado de los servicios
echo "📊 Verificando estado de los servicios..."
docker-compose ps

# Mostrar URLs de acceso
echo ""
echo "🎉 Servicios iniciados exitosamente!"
echo ""
echo "📍 URLs de acceso:"
echo "   - Backend Clipers: http://localhost:5000"
echo "   - Documentación API: http://localhost:5000/swagger-ui.html"
echo "   - Health Check: http://localhost:5000/actuator/health"
echo ""
echo "🗄️  Base de datos:"
echo "   - PostgreSQL: localhost:5432"
echo "   - Redis: localhost:6379"
echo ""
echo "📝 Para ver los logs en tiempo real:"
echo "   docker-compose logs -f"
echo ""
echo "🛑 Para detener los servicios:"
echo "   ./stop-docker-wsl.sh"
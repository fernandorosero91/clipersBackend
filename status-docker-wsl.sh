#!/bin/bash

# Script para verificar el estado y logs de los servicios de Clipers en WSL con Docker
# Este script debe ejecutarse desde el directorio clipersBackend

echo "📊 Verificando estado de los servicios de Clipers..."

# Verificar si estamos en el directorio correcto
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ No se encontró docker-compose.yml en el directorio actual."
    echo "Por favor, ejecuta este script desde el directorio clipersBackend."
    exit 1
fi

# Mostrar estado de los servicios
echo ""
echo "📋 Estado de los servicios:"
docker-compose ps

# Verificar si los servicios están saludables
echo ""
echo "🏥 Verificando salud de los servicios..."

# Verificar PostgreSQL
if docker-compose exec -T postgres pg_isready -U clipers_user -d clipers_db >/dev/null 2>&1; then
    echo "✅ PostgreSQL: Saludable"
else
    echo "❌ PostgreSQL: No saludable"
fi

# Verificar Redis
if docker-compose exec -T redis redis-cli ping >/dev/null 2>&1; then
    echo "✅ Redis: Saludable"
else
    echo "❌ Redis: No saludable"
fi

# Verificar Backend
if curl -f http://localhost:5000/actuator/health >/dev/null 2>&1; then
    echo "✅ Backend: Saludable"
else
    echo "❌ Backend: No saludable"
fi

# Mostrar URLs de acceso
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

# Preguntar si se quieren ver los logs
read -p "¿Quieres ver los logs en tiempo real? (s/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Ss]$ ]]; then
    echo "📝 Mostrando logs en tiempo real (presiona Ctrl+C para detener)..."
    docker-compose logs -f
fi
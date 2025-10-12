#!/bin/bash

# Script para detener los servicios de Clipers en WSL con Docker
# Este script debe ejecutarse desde el directorio clipersBackend

echo "🛑 Deteniendo servicios de Clipers en WSL con Docker..."

# Verificar si estamos en el directorio correcto
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ No se encontró docker-compose.yml en el directorio actual."
    echo "Por favor, ejecuta este script desde el directorio clipersBackend."
    exit 1
fi

# Detener los servicios
echo "🐳 Deteniendo contenedores..."
docker-compose down

# Opcional: Eliminar volúmenes (descomenta si quieres eliminar los datos)
# echo "🗑️  Eliminando volúmenes de datos..."
# docker-compose down -v

echo "✅ Servicios detenidos exitosamente!"
echo ""
echo "📝 Para iniciar los servicios nuevamente:"
echo "   ./start-docker-wsl.sh"
echo ""
echo "📊 Para verificar el estado de los servicios:"
echo "   docker-compose ps"
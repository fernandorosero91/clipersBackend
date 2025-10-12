# Guía de Docker Compose para Clipers Backend

## Requisitos Previos

- Docker instalado en WSL (Ubuntu 20.04)
- Docker Compose instalado
- Las imágenes `postgres:15` y `redis:7-alpine` descargadas localmente

## Importante: Ejecución en WSL

⚠️ **Los scripts de WSL deben ejecutarse desde WSL (Ubuntu), no desde PowerShell en Windows.**

### Pasos correctos para ejecutar los scripts:

1. **Abrir WSL** desde el menú de inicio de Windows o ejecutando:
   ```bash
   wsl
   ```

2. **Navegar al directorio del proyecto**:
   ```bash
   cd /mnt/c/Users/edben/OneDrive/Desktop/Clippers/clipersBackend
   ```

3. **Dar permisos de ejecución** (opcional pero recomendado):
   ```bash
   chmod +x start-docker-wsl.sh stop-docker-wsl.sh status-docker-wsl.sh
   ```

4. **Ejecutar los scripts**:
   ```bash
   ./start-docker-wsl.sh
   ```

### Alternativa: Usar Docker Desktop directamente

Si prefieres no usar los scripts de WSL, puedes usar Docker Desktop directamente:

```bash
# Desde WSL o PowerShell
cd /mnt/c/Users/edben/OneDrive/Desktop/Clippers/clipersBackend

# Levantar servicios
docker-compose up -d --build

# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

Para más detalles sobre la configuración de WSL, consulta [WSL_SETUP_INSTRUCTIONS.md](WSL_SETUP_INSTRUCTIONS.md).

## Configuración del Entorno

### Variables de Entorno Opcionales

Puedes crear un archivo `.env` en el directorio `clipersBackend` para personalizar las siguientes variables:

```bash
# Contraseña segura para PostgreSQL (por defecto: ClipersSecurePass123!)
POSTGRES_PASSWORD=TuContraseñaSegura123!

# Clave secreta para JWT (por defecto: mySuperSecretKey...)
JWT_SECRET=TuClaveSecretaSuperSegura123456789012345678901234567890123456789012345678901234567890
```

### Archivos de Configuración

- `docker-compose.yml`: Configuración principal de los servicios
- `scripts/init-db.sql`: Script de inicialización de la base de datos
- `Dockerfile`: Configuración del contenedor del backend
- `src/main/resources/application-docker.properties`: Configuración de la aplicación para Docker

## Comandos Esenciales

### 1. Levantar todos los servicios

```bash
# Levantar todos los servicios en segundo plano
docker-compose up -d

# Levantar todos los servicios en primer plano (para ver logs en tiempo real)
docker-compose up
```

### 2. Detener todos los servicios

```bash
# Detener y eliminar contenedores, redes y volúmenes
docker-compose down

# Detener sin eliminar volúmenes (para conservar datos)
docker-compose down -v
```

### 3. Verificar el estado de los contenedores

```bash
# Ver estado de todos los servicios
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend
docker-compose logs -f postgres
docker-compose logs -f redis

# Ver estadísticas de los contenedores
docker-compose stats
```

### 4. Reiniciar servicios

```bash
# Reiniciar todos los servicios
docker-compose restart

# Reiniciar un servicio específico
docker-compose restart backend
```

### 5. Actualizar servicios

```bash
# Reconstruir y reiniciar todos los servicios
docker-compose up -d --build

# Reconstruir y reiniciar un servicio específico
docker-compose up -d --build backend
```

## Acceso a los Servicios

### PostgreSQL

- **Host**: `localhost`
- **Puerto**: `5432`
- **Base de datos**: `clipers_db`
- **Usuario**: `clipers_user`
- **Contraseña**: Variable `POSTGRES_PASSWORD` (por defecto: `ClipersSecurePass123!`)

### Redis

- **Host**: `localhost`
- **Puerto**: `6379`
- **Sin autenticación** (configuración básica)

### Backend Clipers

- **URL**: `http://localhost:5000`
- **Documentación API**: `http://localhost:5000/swagger-ui.html`
- **Health Check**: `http://localhost:5000/actuator/health`

## Scripts de Inicialización

El script `scripts/init-db.sql` se ejecuta automáticamente cuando el contenedor de PostgreSQL se inicia por primera vez. Este script:

1. Crea extensiones necesarias (`uuid-ossp`, `pgcrypto`)
2. Crea tablas principales (users, companies, jobs, profiles)
3. Crea índices para mejorar el rendimiento
4. Inserta datos iniciales (usuario admin, empresa Clipers, trabajo de ejemplo)

## Gestión de Volúmenes

Los datos persisten en los siguientes volúmenes:

- `postgres_data`: Datos de PostgreSQL
- `redis_data`: Datos de Redis

Para listar volúmenes:

```bash
docker volume ls
```

Para inspeccionar un volumen:

```bash
docker volume inspect clipers_postgres_data
```

## Red entre Contenedores

Todos los contenedores están conectados a la red `clipers-network` con el rango `172.20.0.0/16`. Los servicios se comunican usando sus nombres de servicio:

- PostgreSQL: `postgres:5432`
- Redis: `redis:6379`
- Backend: `backend:5000`

## Solución de Problemas

### Comandos de Depuración

```bash
# Inspeccionar un contenedor específico
docker inspect clipers-postgres

# Ver los procesos dentro de un contenedor
docker top clipers-backend

# Ejecutar un comando dentro de un contenedor
docker exec -it clipers-postgres psql -U clipers_user -d clipers_db

# Ejecutar un comando dentro del backend
docker exec -it clipers-backend bash

# Ver los logs detallados de un contenedor
docker logs clipers-postgres --tail 50
```

### Problemas Comunes

1. **Puertos en uso**: Si los puertos 5432 o 6379 están ocupados, modifica las asignaciones en `docker-compose.yml`
2. **Permisos de archivos**: Asegúrate de que el usuario de Docker tenga acceso a los directorios
3. **Memoria insuficiente**: Ajusta los recursos de Docker Desktop si es necesario

## Recursos Adicionales

- [Documentación oficial de Docker Compose](https://docs.docker.com/compose/)
- [Documentación de PostgreSQL Docker](https://hub.docker.com/_/postgres)
- [Documentación de Redis Docker](https://hub.docker.com/_/redis)
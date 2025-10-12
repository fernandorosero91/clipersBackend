# Instrucciones de Configuración para WSL

## Problema Detectado

El script `start-docker-wsl.sh` no puede ejecutarse directamente desde PowerShell en Windows. Los scripts están diseñados para ser ejecutados en WSL (Ubuntu).

## Solución Correcta

### 1. Abrir WSL

Abre WSL desde el menú de inicio de Windows o ejecuta:

```bash
wsl
```

### 2. Navegar al directorio correcto

Desde WSL, navega al directorio del proyecto:

```bash
cd /mnt/c/Users/edben/OneDrive/Desktop/Clippers/clipersBackend
```

### 3. Ejecutar los scripts

Una vez en WSL, ejecuta los scripts:

```bash
# Dar permisos de ejecución (opcional, pero recomendado)
chmod +x start-docker-wsl.sh stop-docker-wsl.sh status-docker-wsl.sh

# Iniciar los servicios
./start-docker-wsl.sh

# Detener los servicios
./stop-docker-wsl.sh

# Verificar estado
./status-docker-wsl.sh
```

### 4. Alternativa: Usar Docker Desktop directamente

Si prefieres no usar WSL para los scripts, puedes usar Docker Desktop directamente:

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

## Verificación

Después de iniciar los servicios, verifica que estén accesibles:

- Backend: http://localhost:5000
- Documentación API: http://localhost:5000/swagger-ui.html
- Health Check: http://localhost:5000/actuator/health

## Comandos Rápidos

```bash
# Desde WSL
wsl
cd /mnt/c/Users/edben/OneDrive/Desktop/Clippers/clipersBackend
./start-docker-wsl.sh
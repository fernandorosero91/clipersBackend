# Configuración de Docker y Docker Compose para Clipers Backend

## Paso 1: Instalar Docker Desktop

### Requisitos:
- Windows 10 64-bit: Pro, Enterprise, o Education (Build 16299 o posterior)
- Windows 11: Todas las versiones
- Virtualización habilitada en el BIOS

### Pasos de instalación:

1. **Descarga Docker Desktop**:
   - Visita [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)
   - Descarga la versión para Windows

2. **Ejecuta el instalador**:
   - Haz doble clic en el archivo descargado
   - Sigue las instrucciones del asistente de instalación
   - Acepta los términos y condiciones

3. **Configuración post-instalación**:
   - Una vez instalado, Docker Desktop se iniciará automáticamente
   - Verifica que Docker esté ejecutando buscando el icono de ballena en la bandeja del sistema
   - Haz clic derecho en el icono y selecciona "Settings"

4. **Configuración recomendada**:
   - En "General", asegúrate de que "Use WSL 2 based engine" esté marcado
   - En "Resources" > "WSL Integration", habilita la integración con Ubuntu (cuando lo instales)
   - En "Docker Engine", puedes ajustar la memoria asignada (recomendado 4GB o más)

## Paso 2: Instalar Docker Compose

Docker Desktop ya incluye Docker Compose, pero si necesitas la versión standalone:

1. **Descarga Docker Compose**:
   - Abre PowerShell como Administrador
   - Ejecuta:
     ```powershell
     Invoke-WebRequest "https://github.com/docker/compose/releases/latest/download/docker-compose-Windows-x86_64.exe" -OutFile "$env:USERPROFILE\docker-compose.exe"
     ```

2. **Agrega Docker Compose al PATH**:
   - Mueve el ejecutable a una carpeta en tu PATH:
     ```powershell
     move $env:USERPROFILE\docker-compose.exe "C:\Program Files\Docker Compose"
     ```
   - Asegúrate de que "C:\Program Files\Docker Compose" esté en tu PATH

## Paso 3: Verificar la instalación

1. **Abre PowerShell o CMD** como Administrador
2. **Verifica Docker**:
   ```cmd
   docker --version
   docker run hello-world
   ```

3. **Verifica Docker Compose**:
   ```cmd
   docker-compose --version
   ```

## Paso 4: Configurar el proyecto para Docker

Tu proyecto ya tiene configuración para Docker. Los archivos relevantes son:

1. **Configuración de Docker**:
   - [`application-docker.properties`](clipersBackend/src/main/resources/application-docker.properties): Configuración específica para Docker

2. **Posible archivo docker-compose.yml**:
   - Si tienes un archivo `docker-compose.yml` en el proyecto, asegúrate de que esté configurado correctamente

## Paso 5: Ejecutar el proyecto con Docker

1. **Construye la imagen de Docker**:
   ```cmd
   cd clipersBackend
   docker build -t clipers-backend .
   ```

2. **Ejecuta el contenedor**:
   ```cmd
   docker run -p 8080:8080 clipers-backend
   ```

3. **O usa Docker Compose** (si tienes un archivo docker-compose.yml):
   ```cmd
   docker-compose up
   ```

## Paso 6: Solución de problemas comunes

1. **Error "docker-compose command not found"**:
   - Asegúrate de que Docker Desktop esté instalado y ejecutándose
   - Verifica que Docker Compose esté en el PATH
   - Intenta usar `docker compose` (sin guión) en lugar de `docker-compose`

2. **Error de permisos**:
   - Ejecuta PowerShell/CMD como Administrador
   - Verifica que tu usuario esté en el grupo docker-users

3. **Error de WSL**:
   - Asegúrate de que la virtualización esté habilitada en el BIOS
   - Habilita WSL 2 en Windows Features:
     - Abre PowerShell como Administrador
     - Ejecuta: `wsl --install`

4. **Error de memoria**:
   - Aumenta la memoria asignada a Docker Desktop en Settings > Resources

## Paso 7: Alternativa sin Docker

Si prefieres no usar Docker, puedes seguir las instrucciones en [`POSTGRESQL_SETUP.md`](POSTGRESQL_SETUP.md) para instalar PostgreSQL directamente en Windows.

## Resumen de comandos útiles:

```cmd
# Verificar estado de Docker
docker info

# Listar contenedores en ejecución
docker ps

# Listar todas las imágenes
docker images

# Detener un contenedor
docker stop <container_id>

# Eliminar un contenedor
docker rm <container_id>

# Eliminar una imagen
docker rmi <image_id>
```

Una vez que Docker esté instalado y configurado, podrás ejecutar tu proyecto Clipers Backend con la configuración Docker existente.
# Comandos para ejecutar Docker en WSL

## Paso 1: Verificar que Docker esté instalado y ejecutándose

Primero, asegúrate de que Docker Desktop esté instalado y ejecutándose en Windows. Luego, verifica que Docker esté funcionando en WSL:

```bash
# Verificar si Docker está instalado en WSL
docker --version

# Verificar si Docker está ejecutándose
docker info
```

Si Docker no está instalado en WSL, puedes instalarlo con:

```bash
# Actualizar paquetes
sudo apt update

# Instalar dependencias
sudo apt install apt-transport-https ca-certificates curl software-properties-common -y

# Agregar la clave GPG oficial de Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

# Agregar el repositorio de Docker
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

# Actualizar paquetes nuevamente
sudo apt update

# Instalar Docker
sudo apt install docker-ce docker-ce-cli containerd.io -y

# Iniciar y habilitar Docker
sudo systemctl start docker
sudo systemctl enable docker

# Agregar tu usuario al grupo docker para evitar usar sudo
sudo usermod -aG docker $USER

# Cerrar sesión y volver a iniciar para aplicar los cambios
```

## Paso 2: Clonar y ejecutar el proyecto con Docker

```bash
# Navegar al directorio del proyecto
cd /mnt/c/Users/edben/OneDrive/Desktop/Clippers/clipersBackend

# Construir la imagen de Docker
docker build -t clipers-backend .

# Ejecutar el contenedor
docker run -p 8080:8080 clipers-backend
```

## Paso 3: Usar Docker Compose (Recomendado)

```bash
# Navegar al directorio del proyecto
cd /mnt/c/Users/edben/OneDrive/Desktop/Clippers/clipersBackend

# Construir y ejecutar todos los servicios
docker-compose up --build -d

# Verificar que los servicios están ejecutándose
docker-compose ps

# Ver los logs
docker-compose logs -f

# Detener todos los servicios
docker-compose down
```

## Paso 4: Configuración de red para acceso desde Windows

Si necesitas acceder a los servicios desde Windows, asegúrate de que los puertos estén mapeados correctamente. En el archivo `docker-compose.yml`, los puertos ya están configurados para ser accesibles desde Windows:

- PostgreSQL: 5432
- Redis: 6379
- API: 8080

## Paso 5: Probar la aplicación

Una vez que los servicios estén ejecutándose, puedes acceder a ellos desde tu navegador de Windows:

- API: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health

## Comandos útiles de Docker en WSL

```bash
# Ver contenedores en ejecución
docker ps

# Ver todos los contenedores (incluidos los detenidos)
docker ps -a

# Ver imágenes
docker images

# Detener un contenedor
docker stop <container_id>

# Eliminar un contenedor
docker rm <container_id>

# Eliminar una imagen
docker rmi <image_id>

# Ver logs de un contenedor
docker logs <container_id>

# Entrar a un contenedor en ejecución
docker exec -it <container_id> /bin/bash
```

## Solución de problemas comunes

1. **Error "Cannot connect to the Docker daemon"**:
   - Asegúrate de que Docker Desktop esté ejecutándose en Windows
   - Reinicia Docker Desktop
   - Verifica que tu usuario esté en el grupo docker

2. **Error de permisos**:
   - Asegúrate de haber cerrado y vuelto a iniciar sesión después de agregar tu usuario al grupo docker
   - Intenta ejecutar con sudo: `sudo docker <comando>`

3. **Error de red**:
   - Verifica que los puertos no estén ocupados
   - Asegúrate de que Docker Desktop esté configurado para usar WSL 2

4. **Error de construcción de imagen**:
   - Verifica que el Dockerfile esté en el directorio correcto
   - Asegúrate de que todos los archivos necesarios estén presentes

## Configuración de Docker Desktop para WSL

1. Abre Docker Desktop
2. Ve a Settings > General
3. Asegúrate de que "Use WSL 2 based engine" esté marcado
4. Ve a Settings > Resources > WSL Integration
5. Habilita la integración con Ubuntu
6. Reinicia Docker Desktop si es necesario
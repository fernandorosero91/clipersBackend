# Comandos para configurar PostgreSQL en WSL

## Paso 1: Iniciar Ubuntu por primera vez

Abre Ubuntu desde el menú de inicio de Windows o ejecuta en PowerShell/CMD:
```bash
wsl -d Ubuntu
```

Sigue las instrucciones para configurar tu usuario y contraseña.

## Paso 2: Instalar y configurar PostgreSQL

Una vez dentro de Ubuntu, ejecuta los siguientes comandos:

```bash
# Actualizar paquetes del sistema
sudo apt update && sudo apt upgrade -y

# Instalar PostgreSQL
sudo apt install postgresql postgresql-contrib -y

# Iniciar el servicio PostgreSQL
sudo service postgresql start

# Configurar PostgreSQL para permitir conexiones locales
sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'postgres';"

# Crear la base de datos y el usuario para el proyecto
sudo -u postgres psql -c "CREATE DATABASE clipers_db;"
sudo -u postgres psql -c "CREATE USER clipers_user WITH PASSWORD 'clipers_password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE clipers_db TO clipers_user;"

# Habilitar conexiones locales
echo "host    all             all             127.0.0.1/32            md5" | sudo tee -a /etc/postgresql/*/main/pg_hba.conf
sudo service postgresql restart

# Probar la conexión
sudo -u postgres psql -c "\l"
```

## Paso 3: Clonar y ejecutar el proyecto

```bash
# Navegar al directorio del proyecto (asegúrate de que esté accesible desde WSL)
cd /mnt/c/Users/edben/OneDrive/Desktop/Clippers/clipersBackend

# Dar permisos de ejecución a Maven Wrapper
chmod +x mvnw

# Ejecutar la aplicación con el perfil de PostgreSQL
./mvnw spring-boot:run -Dspring-boot.run.profiles=local-postgres
```

## Paso 4: Probar la aplicación

Abre tu navegador de Windows y visita:
- API: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health

## Comandos útiles para PostgreSQL en WSL

```bash
# Iniciar/Reiniciar/Detener PostgreSQL
sudo service postgresql start
sudo service postgresql restart
sudo service postgresql stop

# Conectarse a PostgreSQL
sudo -u postgres psql

# Conectarse a la base de datos del proyecto
psql -U clipers_user -d clipers_db

# Verificar si PostgreSQL está escuchando
sudo netstat -tlnp | grep postgresql
```

## Solución de problemas

1. **Error de conexión**: Verifica que PostgreSQL esté ejecutando con `sudo service postgresql status`
2. **Permisos**: Asegúrate de que el usuario tenga los permisos necesarios
3. **Puerto ocupado**: Verifica que el puerto 5782 no esté en uso
4. **Acceso denegado**: Revisa el archivo pg_hba.conf para asegurar que las conexiones locales estén permitidas
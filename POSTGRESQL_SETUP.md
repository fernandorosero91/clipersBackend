# Configuración de PostgreSQL para Clipers Backend

## Opción 1: Instalación en Windows (Recomendada por ahora)

### Paso 1: Instalar PostgreSQL

1. Descarga PostgreSQL desde [https://www.postgresql.org/download/windows/](https://www.postgresql.org/download/windows/)
2. Ejecuta el instalador y sigue estos pasos:
   - Selecciona el directorio de instalación (predeterminado está bien)
   - Ingresa una contraseña para el usuario `postgres`
   - Especifica el puerto (5432 es el predeterminado)
   - Selección el idioma
   - En "Locale Selection", selecciona `Spanish_Colombia.1252`
   - Completa la instalación

### Paso 2: Iniciar el servicio PostgreSQL

1. Presiona `Win + R`, escribe `services.msc` y presiona Enter
2. Busca "postgresql-x64-XX" (donde XX es la versión)
3. Haz clic derecho y selecciona "Iniciar"

### Paso 3: Crear la base de datos y usuario

1. Abre pgAdmin 4 (se instaló con PostgreSQL)
2. Conéctate con el usuario `postgres` y la contraseña que configuraste
3. Haz clic derecho en "Databases" y selecciona "Create" > "Database"
4. Ingresa los siguientes valores:
   - **Database name**: `clipers_db`
   - **Owner**: `postgres`
   - **Collation**: `Spanish_Colombia.1252`
5. Haz clic en "Save"
6. Haz clic derecho en el servidor PostgreSQL y selecciona "Query Tool"
7. Ejecuta las siguientes consultas:
   ```sql
   CREATE USER clipers_user WITH PASSWORD 'clipers_password';
   GRANT ALL PRIVILEGES ON DATABASE clipers_db TO clipers_user;
   ```

### Paso 4: Probar la conexión

1. Ejecuta el script de prueba de conexión:
   ```
   test-db-connection.bat
   ```

### Paso 5: Iniciar la aplicación

1. Ejecuta el script para iniciar la aplicación con PostgreSQL:
   ```
   start-with-postgres.bat
   ```

## Opción 2: Instalación con WSL (Cuando habilites virtualización)

### Paso 1: Habilitar virtualización en BIOS

1. Reinicia tu computadora y presiona la tecla para entrar al BIOS (usualmente F2, F10, F12, o Supr durante el arranque)
2. Busca la opción de virtualización:
   - "SVM Mode" (para AMD)
   - "Intel VT-x" o "Intel Virtualization Technology" (para Intel)
3. Habilita la opción y guarda los cambios

### Paso 2: Instalar WSL y Ubuntu

1. Abre PowerShell como Administrador y ejecuta:
   ```
   wsl --install -d Ubuntu
   ```

### Paso 3: Configurar PostgreSQL en WSL

1. Abre Ubuntu desde el menú de inicio
2. Ejecuta los siguientes comandos:
   ```bash
   sudo apt update && sudo apt upgrade -y
   sudo apt install postgresql postgresql-contrib -y
   sudo service postgresql start
   sudo -u postgres psql
   CREATE DATABASE clipers_db;
   CREATE USER clipers_user WITH PASSWORD 'clipers_password';
   GRANT ALL PRIVILEGES ON DATABASE clipers_db TO clipers_user;
   \q
   ```

### Paso 4: Configurar la conexión remota (opcional)

1. Edita el archivo de configuración de PostgreSQL:
   ```bash
   sudo nano /etc/postgresql/*/main/postgresql.conf
   ```
2. Cambica `listen_addresses = 'localhost'`
3. Edita el archivo pg_hba.conf:
   ```bash
   sudo nano /etc/postgresql/*/main/pg_hba.conf
   ```
4. Agrega la línea: `host    all             all             0.0.0.0/0               md5`
5. Reinicia PostgreSQL:
   ```bash
   sudo service postgresql restart
   ```

### Paso 5: Probar la conexión

1. Ejecuta el script de prueba de conexión desde Windows:
   ```
   test-db-connection.bat
   ```

## Configuración de la aplicación

La aplicación ya está configurada para usar PostgreSQL. Los archivos de configuración relevantes son:

- `application.yml`: Configuración principal
- `application-local-postgres.properties`: Configuración específica para PostgreSQL local

Para ejecutar la aplicación con PostgreSQL, usa el perfil `local-postgres`:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local-postgres
```

## Solución de problemas

1. **Error de conexión**: Verifica que PostgreSQL esté ejecutándose y que los datos de conexión sean correctos
2. **Error de permisos**: Asegúrate de que el usuario tenga los permisos necesarios en la base de datos
3. **Error de puerto**: Verifica que el puerto 5432 no esté en uso por otra aplicación

## Notas adicionales

- La contraseña del usuario `clipers_user` es `clipers_password`
- El nombre de la base de datos es `clipers_db`
- El servidor PostgreSQL escucha en `localhost:5432`
# Clipers Backend

Backend de la plataforma Clipers. API REST construida con Spring Boot que gestiona autenticación, perfiles ATS, publicaciones, empleos y procesamiento externo de video.

## Stack

- Spring Boot 3.2.x
- Java 21 (runtime; el proyecto puede compilar a target 17 según pom actual, no cambiar ahora)
- Maven
- PostgreSQL
- Redis
- Docker y Docker Compose

## Requisitos previos

- Java 21 instalado y disponible en PATH
- Maven o Maven Wrapper (mvnw) disponible
- Docker Desktop y Docker Compose
- PostgreSQL/Redis locales si ejecutas sin Docker

## Compilación local

```bash
mvn clean install
```

## Ejecución con Docker

```bash
docker compose -f clipersBackend/docker-compose.yml up -d
```

- Backend disponible en: http://localhost:8080
- Servicios definidos en docker compose:
  - Backend (puerto 8080)
  - PostgreSQL (puerto 5432) — DB: clipers_db, usuario: clipers_user
  - Redis (puerto 6379)
- Montajes de carpetas:
  - ./uploads → /app/uploads
  - ./logs → /app/logs

## Perfiles y entorno

- En contenedor: SPRING_PROFILES_ACTIVE=docker
- Perfiles locales disponibles:
  - local-8080 (levanta en 8080 con configuración local)
  - local-postgres (conexión a PostgreSQL local)
- Variables de entorno (solo nombres, configurar según tu entorno):
  - SPRING_DATASOURCE_URL
  - SPRING_DATASOURCE_USERNAME
  - SPRING_DATASOURCE_PASSWORD
  - SPRING_DATA_REDIS_HOST
  - SPRING_DATA_REDIS_PORT
  - SERVER_PORT
  - JWT_SECRET
  - CORS_ALLOWED_ORIGINS
  - VIDEO_PROCESSING_SERVICE_URL
  - SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE
  - SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE
  - FILE_UPLOAD_DIR

## Endpoints principales (resumen)

- Auth: /api/auth/**
- Clipers: /api/clipers/**
- Jobs: /api/jobs/**
- Posts: /api/posts/**
- ATS Profiles: /api/ats-profiles/**
- Test/health: /api/test/**
- Integración AI: /integration/** (aparece en Swagger como grupo adicional)

## Swagger / OpenAPI

- UI: /swagger-ui.html
- Docs JSON: /api-docs

## Microservicio externo de video

- Base URL configurable por entorno: VIDEO_PROCESSING_SERVICE_URL
  - Default de referencia (no productivo): https://micoservicioprocesarvideo.onrender.com
- Health:
  ```bash
  curl -i https://micoservicioprocesarvideo.onrender.com/
  ```
- Upload (multipart/form-data):
  ```bash
  curl -X POST https://micoservicioprocesarvideo.onrender.com/upload-video -F "file=@ruta/al/video.mp4"
  ```
- Ejemplo de respuesta exitosa (formato de referencia):
  ```json
  {
    "status": "SUCCESS",
    "message": "Video procesado correctamente",
    "score": 0.87,
    "duration": 123.45,
    "metadata": {
      "format": "mp4",
      "resolution": "1920x1080"
    }
  }
  ```
- Notas:
  - Timeouts y reintentos: el cliente HTTP usa timeouts, reintentos automáticos no implementados.
  - Manejo de errores: BusinessException con logging estructurado; revisar logs montados en ./logs.

## CORS

Configurable por properties/env. Variable recomendada:
- CORS_ALLOWED_ORIGINS (ejemplos: http://localhost:3000,http://localhost:5173,http://localhost:8080)

## Logs y uploads

- Los contenedores montan:
  - ./uploads → /app/uploads
  - ./logs → /app/logs
- Ver logs con Docker:
  ```bash
  docker compose -f clipersBackend/docker-compose.yml logs -f
  ```

## Troubleshooting

- Permisos de DB (“permiso denegado al esquema public”):
  - Verifica que el usuario configurado tenga privilegios adecuados en la base de datos y esquema public.
- Healthchecks de Redis/DB en compose:
  - docker compose ps y docker compose logs para revisar estado de servicios.
- Puerto 8080 en uso:
  - Asegúrate de no tener otra app usando el 8080 antes de levantar el backend o ajusta temporalmente SERVER_PORT en tu entorno local (no cambiar configuración por defecto del repo).

## Despliegue (Coolify - notas breves)

- Perfil recomendado: SPRING_PROFILES_ACTIVE=coolify
- Configurar variables de entorno de DB/Redis/JWT/CORS y video.
- Refiérete a application-coolify.properties para nombres de variables soportadas por el perfil de despliegue.

## Scripts útiles (Windows)

- Arranque local perfil 8080: start-backend-local-8080.bat
- Arranque con Docker: start-with-docker.bat
- Probar backend 8080: test-backend-8080.bat
- Probar conexión DB local: test-db-connection.bat
- Instalar Maven (Chocolatey): install-maven.bat

## Recordatorio

Para levantar todo con Docker:
```bash
docker compose -f clipersBackend/docker-compose.yml up -d
```

## Créditos y licencia

- Proyecto académico/experimental para demostración de funcionalidades.
- Licencia: MIT (si aplica en el repositorio).

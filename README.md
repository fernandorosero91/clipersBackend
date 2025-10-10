# Clipers Backend - API REST con Spring Boot

## 📋 Descripción

Backend de la plataforma Clipers, una red social de empleos académica que permite a usuarios crear perfiles profesionales mediante videos cortos (Clipers) procesados con IA para generar perfiles ATS automáticos.

## 🏗️ Arquitectura y Patrones de Diseño

### Patrones Implementados (Implícitamente en el Código)

#### Patrones Creacionales
- **Singleton**: `DatabaseConfig` - Configuración central de BD
- **Factory Method**: `User.createCandidate()`, `User.createCompany()`, `User.createAdmin()` - Métodos estáticos para creación de usuarios
- **Builder**: `ATSProfile.withSummary()`, `addEducation()`, etc. - Métodos fluidos para construcción de perfiles

#### Patrones Estructurales
- **Facade**: Controladores proporcionan interfaz simplificada a servicios complejos
- **Adapter**: Métodos `convertToDTO()` en servicios - Adaptación entre entidades y DTOs

#### Patrones de Comportamiento
- **Observer**: `NotificationService` con handlers internos - Sistema de notificaciones
- **Strategy**: `JobService.calculateSkillMatchScore()`, `calculateExperienceMatchScore()` - Diferentes algoritmos de matching
- **Chain of Responsibility**: `Cliper.processVideo()` - Pipeline interno de procesamiento
- **Template Method**: `UserService.registerUser()`, `AuthService.login()` - Algoritmos con pasos definidos
- **State**: `Cliper.canBeEdited()`, `isProcessingComplete()` - Comportamiento según estado
- **Mediator**: `PostService` - Coordina interacciones entre posts, comentarios y likes

### Estructura de Datos
- **HashMap**: Repositorios JPA para indexación por ID
- **Listas**: Manejo de colecciones de entidades relacionadas
- **Colas**: Procesamiento asíncrono de Clipers (simulado)

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **PostgreSQL**
- **Redis** (caché y sesiones)
- **RabbitMQ** (colas de mensajes)
- **Docker & Docker Compose**
- **Maven**

## 📦 Estructura del Proyecto

```
src/main/java/com/clipers/clipers/
├── entity/          # Entidades JPA
├── repository/      # Repositorios de datos
├── service/         # Lógica de negocio
├── controller/      # Controladores REST
├── dto/            # Data Transfer Objects
├── config/         # Configuraciones
├── security/       # Seguridad JWT
├── factory/        # Factory Methods
├── builder/        # Builder Pattern
├── strategy/       # Strategy Pattern
├── processor/      # Chain of Responsibility
├── observer/       # Observer Pattern
└── ClipersApplication.java
```

## 🗄️ Modelo de Datos

### Entidades Principales
- **User**: Usuarios del sistema (candidatos, empresas, admin)
- **Company**: Perfiles de empresa
- **Cliper**: Videos profesionales de candidatos
- **Job**: Ofertas laborales
- **Post**: Publicaciones del feed social
- **Comment**: Comentarios en publicaciones
- **ATSProfile**: Perfiles generados automáticamente
- **JobMatch**: Coincidencias candidato-empleo

## 🔐 Seguridad

- **JWT Authentication**: Tokens de acceso y refresh
- **Role-based Access Control**: CANDIDATE, COMPANY, ADMIN
- **CORS**: Configurado para desarrollo frontend
- **Password Encoding**: BCrypt

## 📡 API Endpoints

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/refresh` - Refrescar token
- `GET /api/auth/me` - Usuario actual

### Clipers
- `POST /api/clipers` - Crear cliper
- `GET /api/clipers/{id}` - Obtener cliper
- `GET /api/clipers/public` - Clipers públicos (paginado)
- `GET /api/clipers/search` - Buscar clipers
- `PUT /api/clipers/{id}` - Actualizar cliper
- `DELETE /api/clipers/{id}` - Eliminar cliper

### Empleos
- `POST /api/jobs` - Crear empleo (solo empresas)
- `GET /api/jobs/{id}` - Obtener empleo
- `GET /api/jobs/public` - Empleos activos (paginado)
- `GET /api/jobs/search` - Buscar empleos
- `GET /api/jobs/filter` - Filtrar empleos
- `GET /api/jobs/matches/user/{userId}` - Matches de usuario

## 🚀 Configuración Local

### Opción 1: PostgreSQL Local (Recomendada si no tienes Docker)

1. **Instalar PostgreSQL** en tu máquina local
   - Sigue las instrucciones en [POSTGRESQL_SETUP.md](POSTGRESQL_SETUP.md)

2. **Ejecutar la aplicación**
   ```bash
   # Usar Maven
   mvn spring-boot:run -Dspring-boot.run.profiles=local-postgres
   
   # O usar el script
   start-with-postgres.bat
   ```

3. **Probar la conexión**
   ```bash
   # Usar el script de prueba
   test-db-connection.bat
   ```

### Opción 2: WSL (Requiere habilitar virtualización en BIOS)

1. **Habilitar virtualización** en tu BIOS
2. **Instalar WSL y Ubuntu**
   - Sigue las instrucciones en [POSTGRESQL_SETUP.md](POSTGRESQL_SETUP.md)
3. **Configurar PostgreSQL en WSL**
   - Sigue las instrucciones en la sección de WSL del archivo

## 🐳 Despliegue con Docker

### Opción 1: Docker Compose (Recomendado)

1. **Instalar Docker Desktop** (si no lo tienes)
   - Sigue las instrucciones en [DOCKER_SETUP.md](DOCKER_SETUP.md)

2. **Ejecutar con Docker Compose**
   ```bash
   docker-compose up -d
   ```

Esto levanta:
- PostgreSQL (puerto 5432)
- Redis (puerto 6379)
- Spring Boot App (puerto 8080)

3. **Usar el script de inicio** (alternativa)
   ```bash
   start-with-docker.bat
   ```

### Opción 2: Docker Manual

1. **Construir la imagen**
   ```bash
   docker build -t clipers-backend .
   ```

2. **Ejecutar el contenedor**
   ```bash
   docker run -p 8080:8080 clipers-backend
   ```

### Servicios Disponibles

- **API**: http://localhost:8080
- **PostgreSQL**: localhost:5432
- **Redis**: localhost:6379
- **Health Check**: http://localhost:8080/actuator/health

## 🔧 Configuración

### Variables de Entorno

```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/clipers_db
SPRING_DATASOURCE_USERNAME=clipers_user
SPRING_DATASOURCE_PASSWORD=clipers_password

# JWT
JWT_SECRET=mySecretKey123456789012345678901234567890123456789012345678901234567890

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Compilar sin tests
mvn clean package -DskipTests
```

## 🔄 Procesamiento de Clipers

El sistema implementa un pipeline de procesamiento usando **Chain of Responsibility** implícitamente en `Cliper.processVideo()`:

1. **extractAudio()**: Extrae audio del video (simulado)
2. **transcribeAudio()**: Transcribe audio a texto usando Whisper/Vosk (simulado)
3. **analyzeTextAndExtractSkills()**: Analiza texto con NLP y extrae habilidades (simulado)

## 🎯 Matching de Empleos

Sistema de matching usando **Strategy Pattern** implementado implícitamente en `JobService`:

- **calculateSkillMatchScore()**: Estrategia de coincidencia por habilidades
- **calculateExperienceMatchScore()**: Estrategia de coincidencia por experiencia
- **calculateLocationMatchScore()**: Estrategia de coincidencia por ubicación
- **calculateOverallMatchScore()**: Combina múltiples estrategias con pesos

## 📬 Sistema de Notificaciones

Implementado con **Observer Pattern** implícitamente en `NotificationService`:

- **EmailNotificationHandler**: Clase interna para notificaciones por email
- **InAppNotificationHandler**: Clase interna para notificaciones in-app  
- **PushNotificationHandler**: Clase interna para push notifications
- **notifyAllHandlers()**: Notifica a todos los observadores registrados

## 🚧 Próximas Características

- [ ] Integración con servicios de IA reales (Whisper, Hugging Face)
- [ ] WebSockets para notificaciones en tiempo real
- [ ] Elasticsearch para búsqueda semántica
- [ ] Métricas y monitoreo con Actuator
- [ ] Tests unitarios e integración
- [ ] CI/CD pipeline

## 📝 Notas de Desarrollo

- Los servicios de IA están simulados para demostración
- El matching automático se ejecuta de forma síncrona (en producción sería asíncrono)
- Los IDs de usuario se obtienen del contexto de seguridad JWT
- Las notificaciones por email están simuladas (consola)

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/nueva-caracteristica`)
3. Commit cambios (`git commit -am 'Agregar nueva característica'`)
4. Push al branch (`git push origin feature/nueva-caracteristica`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles.

# Clipers Backend - Despliegue en Coolify

## 🚀 Guía de Despliegue

### **1. Preparación del Repositorio**

Asegúrate de que tu repositorio Git contenga:
- ✅ `Dockerfile`
- ✅ `pom.xml`
- ✅ `src/` (código fuente)
- ✅ `coolify-backend.json` (configuración)
- ✅ `application-coolify.properties` (config producción)

### **2. Configuración en Coolify**

#### **Crear Aplicación:**
1. **Nuevo Proyecto** → **Desde Git**
2. **Repositorio:** `tu-repo/backend`
3. **Tipo:** Docker
4. **Puerto:** `8080`
5. **Dockerfile:** `Dockerfile`

#### **Build Settings:**
- **Build Command:** `./mvnw clean package -DskipTests`
- **Build Context:** `/backend` (si está en subdirectorio)

### **3. Variables de Entorno Requeridas**

```env
# Perfil de Spring
SPRING_PROFILES_ACTIVE=coolify

# Puerto
SERVER_PORT=8080

# Base de Datos (configurar después de crear servicio PostgreSQL)
DATABASE_URL=postgresql://postgres-service:5432/clipers_db
DATABASE_USERNAME=clipers_user  
DATABASE_PASSWORD=tu_password_seguro

# JWT (generar secret seguro)
JWT_SECRET=tu_jwt_secret_muy_largo_y_seguro_de_al_menos_64_caracteres

# CORS (actualizar con tu dominio)
CORS_ALLOWED_ORIGINS=https://tu-frontend.coolify.app,http://localhost:3000

# Opcional - Redis
REDIS_HOST=redis-service
REDIS_PORT=6379

# Opcional - RabbitMQ  
RABBITMQ_HOST=rabbitmq-service
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=clipers
RABBITMQ_PASSWORD=clipers123
```

### **4. Servicios Adicionales**

#### **PostgreSQL (Requerido):**
1. **Agregar Servicio** → **PostgreSQL 15**
2. **Configuración:**
   ```
   Database: clipers_db
   Username: clipers_user
   Password: [generar seguro]
   ```
3. **Obtener URL de conexión** y actualizar `DATABASE_URL`

#### **Redis (Opcional):**
1. **Agregar Servicio** → **Redis 7**
2. **Actualizar** `REDIS_HOST` con el nombre del servicio

#### **RabbitMQ (Opcional):**
1. **Agregar Servicio** → **RabbitMQ 3**
2. **Configurar** usuario y password
3. **Actualizar** variables RABBITMQ_*

### **5. Health Check**

Coolify verificará la salud en:
- **URL:** `https://tu-backend.coolify.app/api/test/health`
- **Intervalo:** 30s
- **Timeout:** 10s
- **Reintentos:** 3

### **6. Dominios y DNS**

1. **Dominio Automático:** `tu-app.coolify.app`
2. **Dominio Personalizado:** Configurar en Coolify
3. **HTTPS:** Automático con Let's Encrypt

### **7. Logs y Monitoreo**

```bash
# Ver logs en tiempo real
Coolify Dashboard → Tu App → Logs

# Métricas disponibles
- CPU y Memoria
- Requests por minuto
- Tiempo de respuesta
- Errores
```

### **8. Actualización y CI/CD**

#### **Despliegue Manual:**
1. Push código a Git
2. Coolify → **Deploy** (botón)

#### **Despliegue Automático:**
1. **Settings** → **Auto Deploy**
2. **Branch:** `main` o `production`
3. **Webhook:** Configurar en GitHub/GitLab

### **9. Troubleshooting**

#### **Error de Conexión a DB:**
```bash
# Verificar variables de entorno
DATABASE_URL=postgresql://postgres-service:5432/clipers_db

# Verificar que PostgreSQL esté corriendo
Coolify → Services → PostgreSQL → Status
```

#### **Error de CORS:**
```bash
# Actualizar dominio en variables
CORS_ALLOWED_ORIGINS=https://tu-frontend.coolify.app
```

#### **Error de Build:**
```bash
# Verificar Java version en Dockerfile
FROM openjdk:21-jdk-slim

# Verificar Maven wrapper
./mvnw clean package -DskipTests
```

### **10. URLs Finales**

Después del despliegue:
- **API Backend:** `https://tu-backend.coolify.app/api`
- **Health Check:** `https://tu-backend.coolify.app/api/test/health`
- **Swagger (si habilitado):** `https://tu-backend.coolify.app/swagger-ui.html`

### **11. Conectar con Frontend**

Actualizar en tu frontend:
```env
NEXT_PUBLIC_API_URL=https://tu-backend.coolify.app/api
```

## 🔒 Seguridad

- ✅ Usuario no-root en Docker
- ✅ Variables de entorno para secrets
- ✅ HTTPS automático
- ✅ JWT con secret seguro
- ✅ CORS configurado correctamente

## 📊 Recursos Recomendados

- **CPU:** 1 vCore
- **RAM:** 1GB (mínimo)
- **Storage:** 10GB
- **PostgreSQL:** 512MB RAM

¡Tu backend estará listo para producción! 🎉

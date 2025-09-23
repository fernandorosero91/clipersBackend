# PostgreSQL 17 - Configuración para Clipers Backend

## 🗄️ **Configuración PostgreSQL 17 en Coolify**

### **✅ PASO 1: Crear PostgreSQL 17**

En Coolify:
1. **New Resource** → **Service** → **PostgreSQL 17 (default)**
2. **Configuración:**
   ```
   Service Name: clipers-postgres
   Version: 17 (default)
   Database Name: clipers_db
   Username: clipers_user
   Password: [generar uno seguro, ej: ClipersDB2024!]
   ```

### **🔧 PASO 2: Variables de Entorno para Backend**

Una vez creado PostgreSQL, usar estas variables:

```env
# Database Configuration para PostgreSQL 17
DATABASE_URL=postgresql://clipers-postgres:5432/clipers_db
DATABASE_USERNAME=clipers_user
DATABASE_PASSWORD=ClipersDB2024!

# Configuración específica para PostgreSQL 17
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

### **📋 PASO 3: Configuración Completa del Backend**

Variables de entorno completas para el backend:

```env
# Spring Configuration
SPRING_PROFILES_ACTIVE=coolify
SERVER_PORT=8080

# PostgreSQL 17 Configuration
DATABASE_URL=postgresql://clipers-postgres:5432/clipers_db
DATABASE_USERNAME=clipers_user
DATABASE_PASSWORD=ClipersDB2024!

# JWT Security
JWT_SECRET=MiSecretoJWTMuyLargoYSeguroParaClipersProduccion2024ConMasDe64Caracteres

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://clipers.sufactura.store,http://localhost:3000

# File uploads
FILE_UPLOAD_DIR=./uploads

# JPA/Hibernate for PostgreSQL 17
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
```

### **🚀 PASO 4: Orden de Despliegue**

1. **Crear PostgreSQL 17** primero
2. **Esperar que esté "Running"**
3. **Copiar la URL de conexión** desde el dashboard
4. **Crear aplicación backend** con las variables de entorno
5. **Deploy backend**

### **✅ PASO 5: Verificar Conexión**

Una vez desplegado, verificar:
- **Health check:** `https://backend.sufactura.store/api/test/health`
- **Logs del backend:** Debe mostrar conexión exitosa a PostgreSQL
- **Logs de PostgreSQL:** No debe mostrar errores de conexión

### **🔍 Troubleshooting PostgreSQL 17**

**Error de conexión:**
```bash
# Verificar que el servicio PostgreSQL esté corriendo
Coolify → Services → clipers-postgres → Status: Running

# Verificar logs de PostgreSQL
Coolify → Services → clipers-postgres → Logs
```

**Error de autenticación:**
```bash
# Verificar credenciales en variables de entorno
DATABASE_USERNAME=clipers_user
DATABASE_PASSWORD=tu_password_real
```

### **📊 Ventajas de PostgreSQL 17**

- ✅ **Mejor rendimiento** que versiones anteriores
- ✅ **Nuevas funciones SQL** y optimizaciones
- ✅ **Mejor manejo de JSON** y tipos de datos
- ✅ **Soporte completo** con Spring Boot 3.5.6
- ✅ **Seguridad mejorada**

### **🎯 Configuración Final**

Con PostgreSQL 17, tu stack será:
- **Frontend:** Next.js 14 en `clipers.sufactura.store`
- **Backend:** Spring Boot 3.5.6 en `backend.sufactura.store`
- **Database:** PostgreSQL 17 (interno en Coolify)
- **SSL:** Automático con Let's Encrypt

¡PostgreSQL 17 funcionará perfectamente con tu aplicación! 🎉

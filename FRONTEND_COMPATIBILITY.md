# 🔗 Backend-Frontend Compatibility Guide

## ✅ Endpoints Implementados para el Frontend

### 🔐 **Autenticación** (`/api/auth`)
- ✅ `POST /auth/login` - Login de usuario
- ✅ `POST /auth/register` - Registro de usuario  
- ✅ `POST /auth/refresh` - Refresh token
- ✅ `GET /auth/me` - Usuario actual

### 🎬 **Clipers** (`/api/clipers`)
- ✅ `POST /clipers/upload` - Subir cliper (multipart/form-data)
- ✅ `GET /clipers?page={page}&size={size}` - Listar clipers paginados
- ✅ `GET /clipers/{id}` - Obtener cliper específico
- ✅ `GET /clipers/user/{userId}` - Clipers por usuario
- ✅ `PUT /clipers/{id}` - Actualizar cliper
- ✅ `DELETE /clipers/{id}` - Eliminar cliper
- ✅ `GET /clipers/status/{status}` - Clipers por estado

### 💼 **Empleos** (`/api/jobs`)
- ✅ `GET /jobs?page={page}&size={size}&search={query}` - Buscar empleos
- ✅ `POST /jobs` - Crear empleo (solo empresas)
- ✅ `GET /jobs/{id}` - Obtener empleo específico
- ✅ `PUT /jobs/{id}` - Actualizar empleo
- ✅ `DELETE /jobs/{id}` - Eliminar empleo
- ✅ `GET /jobs/{jobId}/matches` - Matches para un empleo
- ✅ `POST /jobs/{jobId}/apply` - Aplicar a empleo
- ✅ `GET /jobs/matches/user/{userId}` - Matches para usuario

### 📝 **Posts/Feed** (`/api/posts`)
- ✅ `GET /posts?page={page}&size={size}` - Feed principal
- ✅ `POST /posts` - Crear publicación
- ✅ `GET /posts/{id}` - Obtener publicación específica
- ✅ `PUT /posts/{id}` - Actualizar publicación
- ✅ `DELETE /posts/{id}` - Eliminar publicación
- ✅ `POST /posts/{id}/like` - Toggle like
- ✅ `POST /posts/{id}/comments` - Agregar comentario
- ✅ `GET /posts/{id}/comments` - Obtener comentarios
- ✅ `GET /posts/search?query={query}` - Buscar publicaciones

### 👤 **Perfil ATS** (`/api/profile`)
- ✅ `GET /profile/ats` - Obtener perfil ATS del usuario actual
- ✅ `GET /profile/ats/{userId}` - Obtener perfil ATS por usuario
- ✅ `POST /profile/ats` - Crear perfil ATS
- ✅ `PUT /profile/ats` - Actualizar perfil ATS

## 📊 **Formato de Respuestas**

### **Respuestas Paginadas** (coincide con frontend)
```json
{
  "clipers": [...],     // o "jobs", "posts" según endpoint
  "hasMore": boolean,
  "totalPages": number,
  "currentPage": number,
  "totalElements": number
}
```

### **Respuesta de Autenticación**
```json
{
  "accessToken": "string",
  "refreshToken": "string", 
  "user": {
    "id": "string",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "role": "CANDIDATE" | "COMPANY" | "ADMIN",
    "profileImage": "string?",
    "createdAt": "string",
    "updatedAt": "string"
  }
}
```

### **Entidades Principales**
- ✅ **User**: Compatible con interface User del frontend
- ✅ **Cliper**: Compatible con interface Cliper del frontend  
- ✅ **Job**: Compatible con interface Job del frontend
- ✅ **Post**: Compatible con interface Post del frontend
- ✅ **Comment**: Compatible con interface Comment del frontend
- ✅ **ATSProfile**: Compatible con interface ATSProfile del frontend

## 🔄 **Flujos Principales Soportados**

### **1. Registro y Login**
```typescript
// Frontend store
const response = await apiClient.post<AuthResponse>("/auth/login", { email, password })
// ✅ Backend responde con formato correcto
```

### **2. Upload de Cliper**
```typescript
// Frontend store
const formData = new FormData()
formData.append("video", file)
formData.append("title", title) 
formData.append("description", description)
const response = await apiClient.post<Cliper>("/clipers/upload", formData)
// ✅ Backend maneja multipart/form-data correctamente
```

### **3. Carga de Feed**
```typescript
// Frontend store  
const response = await apiClient.get<{posts: Post[], hasMore: boolean}>(`/posts?page=${page}&size=10`)
// ✅ Backend responde con formato de paginación correcto
```

### **4. Búsqueda de Empleos**
```typescript
// Frontend store
const response = await apiClient.get<{jobs: Job[], hasMore: boolean}>(`/jobs?${params}`)
// ✅ Backend maneja todos los filtros del frontend
```

## 🛠️ **Configuración de Desarrollo**

### **CORS**
- ✅ Configurado para `http://localhost:3000` (Next.js)
- ✅ Configurado para `http://localhost:5173` (Vite)

### **Base URL**
- ✅ Backend: `http://localhost:8080/api`
- ✅ Frontend ApiClient configurado correctamente

### **Autenticación JWT**
- ✅ Headers Authorization: `Bearer {token}`
- ✅ Refresh automático de tokens
- ✅ Redirección a login en caso de error 401

## 🚀 **Instrucciones de Uso**

1. **Iniciar Backend:**
   ```bash
   cd backend
   docker-compose up -d
   ```

2. **Iniciar Frontend:**
   ```bash
   npm run dev
   ```

3. **Verificar Conectividad:**
   - Backend: http://localhost:8080
   - Frontend: http://localhost:3000
   - Base de datos: localhost:5432

## ✅ **Estado de Compatibilidad**

- 🟢 **Autenticación**: 100% compatible
- 🟢 **Clipers**: 100% compatible  
- 🟢 **Empleos**: 100% compatible
- 🟢 **Feed Social**: 100% compatible
- 🟢 **Perfiles ATS**: 100% compatible
- 🟢 **Formato de Respuestas**: 100% compatible
- 🟢 **CORS y Headers**: 100% compatible

## 🔍 **Testing de Endpoints**

Todos los endpoints han sido diseñados para coincidir exactamente con las expectativas del frontend TypeScript. Los stores de Zustand funcionarán sin modificaciones adicionales.

# ============================================
# Etapa 1: Construcción (Build)
# ============================================
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Definir el directorio de trabajo
WORKDIR /app

# Copiar el archivo de configuración de Maven
COPY pom.xml .

# Descargar dependencias para cachear esta capa
RUN mvn dependency:go-offline -B

# Copiar el código fuente del proyecto
COPY src ./src

# Compilar la aplicación (sin ejecutar tests)
RUN mvn clean package -DskipTests

# ============================================
# Etapa 2: Producción (Runtime)
# ============================================
FROM eclipse-temurin:21-jdk-jammy

# Crear usuario no root por seguridad
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Crear directorio de la aplicación
WORKDIR /app

# Crear carpeta de uploads y logs
RUN mkdir -p /app/uploads /app/logs && chown -R spring:spring /app

# Copiar el archivo JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar al usuario seguro
USER spring:spring

# Exponer puerto de la aplicación
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Comando de inicio del contenedor
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

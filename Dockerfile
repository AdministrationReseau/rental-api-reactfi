# Multi-stage build pour optimiser l'image
FROM eclipse-temurin:21-jdk-alpine AS build

# Installer Maven
RUN apk add --no-cache maven

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers Maven pour la résolution des dépendances
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Rendre mvnw exécutable
RUN chmod +x mvnw

# Télécharger les dépendances (mise en cache des layers)
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY src src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# ============================================================
# Runtime stage
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# Installer curl pour health checks
RUN apk add --no-cache curl

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S rental && \
    adduser -u 1001 -S rental -G rental

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/rental-api-reactive-*.jar app.jar

# Créer le répertoire des uploads
RUN mkdir -p /app/uploads && \
    chown -R rental:rental /app

# Changer vers l'utilisateur non-root
USER rental

# Exposer le port
EXPOSE 8080

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xms512m -Xmx1024m" \
    TZ=Africa/Douala

# Health check spécifique à WebFlux
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/v1/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

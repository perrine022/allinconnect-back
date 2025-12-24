# Stage 1: Build
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
# Utilisation d'une image stable Eclipse Temurin (recommandée par Docker et Render)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Port utilisé par Render (8080 par défaut pour Spring Boot)
EXPOSE 8080

# Utilisation des variables d'environnement pour la configuration
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]

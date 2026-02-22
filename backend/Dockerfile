# ── Stage 1: Build ──────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Cache dependencies layer first
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build application
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/*.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]

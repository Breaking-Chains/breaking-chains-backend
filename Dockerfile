# Stage 1: Build the application with Gradle
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and configuration files first for efficient caching
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/

# Grant execute permissions for Linux gradlew
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build the production executable JAR
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime environment
FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

# Create a non-root system user and group for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy compiled JAR from builder stage
COPY --from=builder /app/build/libs/breaking-chains-backend-1.0.0.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

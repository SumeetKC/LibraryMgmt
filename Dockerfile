
# Multi-stage Dockerfile for building and running the Spring Boot application
# Build stage: uses Maven to compile and package the application using the project's maven wrapper
FROM eclipse-temurin:17 AS build

# Set workdir and copy only the files needed for dependency resolution first to leverage Docker cache
WORKDIR /workspace

# Copy maven wrapper and pom first for dependency download caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Make the wrapper executable
RUN chmod +x mvnw || true

# Copy source code
COPY src ./src

# Build the application and skip tests to speed up image builds by default
RUN ./mvnw -B -DskipTests package

# Runtime stage: use a smaller JRE image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Expose default Spring Boot port
EXPOSE 8080

# Copy the fat jar from the build stage. Spring Boot's maven plugin will create a jar in target/.
COPY --from=build /workspace/target/*.jar app.jar

# Use a non-root user for better security
RUN useradd --create-home appuser || adduser --disabled-password --gecos "" appuser || true
USER appuser

# Run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]

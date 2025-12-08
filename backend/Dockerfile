# dockerfile
# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy and build the application
COPY pom.xml .
COPY src ./src

# Build the application and verify the JAR was created
RUN mvn -B -DskipTests clean package && \
    echo "Build completed. Checking for JAR file..." && \
    ls -la /app/target/ && \
    test -f /app/target/protube-back-0.0.1-SNAPSHOT.jar || \
    (echo "ERROR: JAR file not found!" && exit 1)

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/protube-back-0.0.1-SNAPSHOT.jar app.jar

# Verify the JAR file was copied and is readable
RUN test -f app.jar && \
    test -r app.jar && \
    echo "JAR file copied successfully: $(ls -lh app.jar)"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

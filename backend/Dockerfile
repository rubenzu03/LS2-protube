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

# Verify the JAR manifest inside the build stage (JDK tools available here)
RUN jar -tf /app/target/protube-back-0.0.1-SNAPSHOT.jar | grep -q "META-INF/MANIFEST.MF" || \
    (echo "ERROR: META-INF/MANIFEST.MF not found in JAR" && exit 1) && \
    jar -xf /app/target/protube-back-0.0.1-SNAPSHOT.jar META-INF/MANIFEST.MF && \
    if grep -E -i '^(Main-Class|Start-Class):' META-INF/MANIFEST.MF >/dev/null 2>&1; then \
      echo "JAR manifest present and contains Main-Class/Start-Class"; \
    else \
      echo "ERROR: Main-Class or Start-Class not found in MANIFEST.MF" && exit 1; \
    fi

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install unzip for JAR verification (minimal overhead)
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from build stage
COPY --from=build /app/target/protube-back-0.0.1-SNAPSHOT.jar app.jar

# Verify the JAR file was copied and is readable
RUN test -f app.jar && \
    test -r app.jar && \
    echo "JAR file copied successfully: $(ls -lh app.jar)"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

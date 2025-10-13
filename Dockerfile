# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/target/*.jar app.jar

# 🟢 Copy your runtime application.properties (this overrides internal JAR config)
COPY src/main/resources/application.properties /app/application.properties

EXPOSE 9097

# 🟢 Tell Spring Boot to use the external file instead of the one packaged inside the JAR
ENTRYPOINT ["java", "-Dspring.config.location=file:/app/application.properties", "-jar", "app.jar"]

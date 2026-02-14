# build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw -q -DskipTests package

COPY src src
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests package

# run
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN mkdir -p /data
COPY --from=builder /app/target/*.jar app.jar

# run (non-root)
RUN useradd -r -u 1001 appuser && chown -R appuser:appuser /app /data
USER appuser

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]
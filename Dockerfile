FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY src src

RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd -r -u 10001 spring && \
    mkdir -p /app/logs /app/files && \
    chown -R spring:spring /app

COPY --from=build /workspace/target/*.jar /app/app.jar

USER spring

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
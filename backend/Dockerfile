# ---- Build: compila o jar com o Gradle da imagem ----
FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle :pedidos-service:bootJar --no-daemon -x test

# ---- Runtime: roda o jar ----
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/pedidos-service/build/libs/*-SNAPSHOT.jar /app/pedidos.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/pedidos.jar"]

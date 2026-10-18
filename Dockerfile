# Etapa 1: Compilação com Gradle
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY . .
# Compila o projeto gerando o JAR executável do Spring Boot
RUN gradle bootJar -x test --no-daemon
# Remove o JAR secundario (-plain) para evitar conflitos na copia
RUN rm -f build/libs/*-plain.jar

# Etapa 2: Execução da aplicação Java
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
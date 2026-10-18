# Etapa 1: Compilação com Gradle e Java 21
FROM gradle:jdk21-alpine AS build
WORKDIR /app
COPY . .
# Compila o projeto gerando o JAR executável do Spring Boot
RUN gradle bootJar -x test --no-daemon
# Remove o JAR secundário (-plain) para evitar conflitos na cópia
RUN rm -f build/libs/*-plain.jar

# Etapa 2: Execução da aplicação Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
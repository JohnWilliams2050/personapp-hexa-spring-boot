# syntax=docker/dockerfile:1

# ---------- Etapa 1: compilar todo el proyecto multi-módulo una sola vez ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
# El montaje de caché mantiene ~/.m2 entre compilaciones, por lo que las dependencias se descargan solo una vez
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests package

# ---------- Etapa 2: entorno de ejecución común (solo JRE, usuario no root) ----------
FROM eclipse-temurin:25-jre AS runtime
RUN groupadd --system app && useradd --system --gid app --home-dir /app app \
    && mkdir -p /app/logs && chown -R app:app /app
WORKDIR /app
USER app
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

# ---------- Objetivo "rest": adaptador de entrada REST (Swagger en 3000) ----------
FROM runtime AS rest
COPY --from=build --chown=app:app /workspace/rest-input-adapter/target/rest-input-adapter-*.jar /app/app.jar
EXPOSE 3000

# ---------- Objetivo "cli": adaptador de entrada CLI (interactivo) ----------
FROM runtime AS cli
COPY --from=build --chown=app:app /workspace/cli-input-adapter/target/cli-input-adapter-*.jar /app/app.jar
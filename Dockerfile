# syntax=docker/dockerfile:1

# ---------- Stage 1: build the whole multi-module project once ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
# The cache mount keeps ~/.m2 between builds, so dependencies download only once
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -DskipTests package

# ---------- Stage 2: common runtime (JRE only, non-root user) ----------
FROM eclipse-temurin:25-jre AS runtime
RUN groupadd --system app && useradd --system --gid app --home-dir /app app \
    && mkdir -p /app/logs && chown -R app:app /app
WORKDIR /app
USER app
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

# ---------- Target "rest": REST input adapter (Swagger on 3000) ----------
FROM runtime AS rest
COPY --from=build --chown=app:app /workspace/rest-input-adapter/target/rest-input-adapter-*.jar /app/app.jar
EXPOSE 3000

# ---------- Target "cli": CLI input adapter (interactive) ----------
FROM runtime AS cli
COPY --from=build --chown=app:app /workspace/cli-input-adapter/target/cli-input-adapter-*.jar /app/app.jar
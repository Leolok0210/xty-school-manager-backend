FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a startup script that waits for the app to be ready
RUN echo '#!/bin/sh\n\
set -e\n\
echo "Starting application..."\n\
java -jar /app/app.jar &\n\
APP_PID=$!\n\
echo "Waiting for app to start (PID: $APP_PID)"\n\
for i in $(seq 1 90); do\n\
  if curl -s http://localhost:8080/ > /dev/null 2>&1; then\n\
    echo "App is ready!"\n\
    break\n\
  fi\n\
  echo "Waiting... ($i/90)"\n\
  sleep 2\n\
done\n\
echo "App startup complete, continuing..."\n\
exec java -jar /app/app.jar\n' > /app/start.sh && chmod +x /app/start.sh

COPY --from=builder /app/target/xty-school-manager.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=180s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

ENTRYPOINT ["java", "-Xmx384m", "-Xms256m", "-jar", "/app/app.jar"]
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -Dbuild.id=rule-derived-status-lazy

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/xty-school-manager.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=180s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

ENTRYPOINT ["java", "-Xmx256m", "-Xss256k", "-XX:MaxMetaspaceSize=128m", "-XX:ReservedCodeCacheSize=64m", "-XX:MaxRAMPercentage=75.0", "-Dspring.servlet.multipart.max-file-size=200MB", "-Dspring.servlet.multipart.max-request-size=200MB", "-jar", "/app/app.jar"]